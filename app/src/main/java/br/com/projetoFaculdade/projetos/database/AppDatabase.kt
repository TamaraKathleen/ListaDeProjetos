package br.com.projetoFaculdade.projetos.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.projetoFaculdade.projetos.database.dao.NotaDao
import br.com.projetoFaculdade.projetos.model.Nota

@Database(
    version = 2,
    entities = [Nota::class],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notaDao(): NotaDao

    companion object {
        @Volatile
        private var db: AppDatabase? = null

        fun instancia(context: Context): AppDatabase {
            return db ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "ceep.db"
            ).fallbackToDestructiveMigration().build()
        }
    }

}