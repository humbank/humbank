import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.scrobotic.humbank.Database

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        //context.deleteDatabase("Humbank.db")

        return AndroidSqliteDriver(Database.Schema, context, "Humbank.db")
    }
}