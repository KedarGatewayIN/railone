package com.indianrailways.railone.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.indianrailways.railone.data.model.AppNotification
import com.indianrailways.railone.data.model.PlatformTicket
import com.indianrailways.railone.data.model.ReservedTicket
import com.indianrailways.railone.data.model.UnreservedTicket

class RailOneDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val gson = Gson()

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_UNRESERVED (
                uts_number TEXT PRIMARY KEY,
                payload_json TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE $TABLE_RESERVED (
                pnr TEXT PRIMARY KEY,
                payload_json TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE $TABLE_PLATFORM (
                id TEXT PRIMARY KEY,
                payload_json TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE $TABLE_NOTIFICATIONS (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                message TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                is_read INTEGER NOT NULL
            )
            """.trimIndent()
        )

        seedDefaultNotifications(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_UNRESERVED")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESERVED")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLATFORM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICATIONS")
        onCreate(db)
    }

    private fun seedDefaultNotifications(db: SQLiteDatabase) {
        val now = System.currentTimeMillis()
        val initial = listOf(
            AppNotification(
                id = "notif_welcome_1",
                title = "RailOne",
                message = "Welcome to RailOne. Your paperless UTS and reserved tickets are stored securely on this device for instant offline verification.",
                timestamp = now - 3600_000L,
                isRead = false
            ),
            AppNotification(
                id = "notif_alert_2",
                title = "RailOne Alert",
                message = "Dear User, please avoid changing your handset or performing factory reset while an active paperless ticket is valid on your phone.",
                timestamp = now - 7200_000L,
                isRead = false
            )
        )
        initial.forEach { n ->
            val values = ContentValues().apply {
                put("id", n.id)
                put("title", n.title)
                put("message", n.message)
                put("timestamp", n.timestamp)
                put("is_read", if (n.isRead) 1 else 0)
            }
            db.insertWithOnConflict(TABLE_NOTIFICATIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    fun upsertUnreservedTicket(ticket: UnreservedTicket) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("uts_number", ticket.utsNumber)
            put("payload_json", gson.toJson(ticket))
            put("created_at", ticket.bookingTimestamp)
        }
        db.insertWithOnConflict(TABLE_UNRESERVED, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllUnreservedTickets(): List<UnreservedTicket> {
        val result = mutableListOf<UnreservedTicket>()
        val db = readableDatabase
        db.rawQuery("SELECT payload_json FROM $TABLE_UNRESERVED ORDER BY created_at DESC", null).use { cursor ->
            while (cursor.moveToNext()) {
                val json = cursor.getString(0)
                runCatching { gson.fromJson(json, UnreservedTicket::class.java) }
                    .getOrNull()
                    ?.let { result.add(it) }
            }
        }
        return result
    }

    fun upsertReservedTicket(ticket: ReservedTicket) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("pnr", ticket.pnr)
            put("payload_json", gson.toJson(ticket))
            put("created_at", ticket.bookingTimestamp)
        }
        db.insertWithOnConflict(TABLE_RESERVED, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllReservedTickets(): List<ReservedTicket> {
        val result = mutableListOf<ReservedTicket>()
        val db = readableDatabase
        db.rawQuery("SELECT payload_json FROM $TABLE_RESERVED ORDER BY created_at DESC", null).use { cursor ->
            while (cursor.moveToNext()) {
                val json = cursor.getString(0)
                runCatching { gson.fromJson(json, ReservedTicket::class.java) }
                    .getOrNull()
                    ?.let { result.add(it) }
            }
        }
        return result
    }

    fun upsertPlatformTicket(ticket: PlatformTicket) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", ticket.id)
            put("payload_json", gson.toJson(ticket))
            put("created_at", ticket.bookingTimestamp)
        }
        db.insertWithOnConflict(TABLE_PLATFORM, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllPlatformTickets(): List<PlatformTicket> {
        val result = mutableListOf<PlatformTicket>()
        val db = readableDatabase
        db.rawQuery("SELECT payload_json FROM $TABLE_PLATFORM ORDER BY created_at DESC", null).use { cursor ->
            while (cursor.moveToNext()) {
                val json = cursor.getString(0)
                runCatching { gson.fromJson(json, PlatformTicket::class.java) }
                    .getOrNull()
                    ?.let { result.add(it) }
            }
        }
        return result
    }

    fun insertNotification(notification: AppNotification) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", notification.id)
            put("title", notification.title)
            put("message", notification.message)
            put("timestamp", notification.timestamp)
            put("is_read", if (notification.isRead) 1 else 0)
        }
        db.insertWithOnConflict(TABLE_NOTIFICATIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllNotifications(): List<AppNotification> {
        val result = mutableListOf<AppNotification>()
        val db = readableDatabase
        db.rawQuery(
            "SELECT id, title, message, timestamp, is_read FROM $TABLE_NOTIFICATIONS ORDER BY timestamp DESC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    AppNotification(
                        id = cursor.getString(0),
                        title = cursor.getString(1),
                        message = cursor.getString(2),
                        timestamp = cursor.getLong(3),
                        isRead = cursor.getInt(4) == 1
                    )
                )
            }
        }
        return result
    }

    fun markAllNotificationsRead() {
        val db = writableDatabase
        val values = ContentValues().apply { put("is_read", 1) }
        db.update(TABLE_NOTIFICATIONS, values, null, null)
    }

    companion object {
        private const val DATABASE_NAME = "railone_bookings.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_UNRESERVED = "unreserved_tickets"
        private const val TABLE_RESERVED = "reserved_tickets"
        private const val TABLE_PLATFORM = "platform_tickets"
        private const val TABLE_NOTIFICATIONS = "notifications"
    }
}
