//package com.example.fitme.data.local
//
//import android.app.Application
//import com.example.fitme.core.utils.Log
//import com.example.fitme.di.koinModules
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.ktx.Firebase
//import org.koin.android.ext.koin.androidContext
//import org.koin.core.context.startKoin
//
//open class AppDatabase: Application() {
//
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // Init Koin DI
//        startKoin {
//            androidContext(this@AppDatabase)
//            modules(koinModules)
//        }
//
//        // Init Timber log
//        Log.init()
//    }
//}
//
////import androidx.room.Database
////import androidx.room.Room
////import androidx.room.RoomDatabase
////import org.koin.android.ext.koin.androidContext
////import org.koin.dsl.module
////import ru.appsstudio.eatrepeat.BuildConfig.DB_VERSION
////import ru.appsstudio.eatrepeat.data.local.db.CategoryDao
////import ru.appsstudio.eatrepeat.data.local.db.ProductsDao
////import ru.appsstudio.eatrepeat.models.entities.Category
////import ru.appsstudio.eatrepeat.models.entities.Product
////
////val dataBaseModule = module{
////    single {
////        Room.databaseBuilder(androidContext(),
////            AppDataBase::class.java, "app-database")
////            .fallbackToDestructiveMigration().build()
////    }
////
////    single { provideProductsDao(get()) }
////    single { provideCategoryDao(get()) }
////}
////
////fun provideProductsDao(db: AppDataBase) = db.productsDao()
////fun provideCategoryDao(db: AppDataBase) = db.categoryDao()
////
////@Database(entities = [Product::class, Category::class], version = DB_VERSION, exportSchema = false)
////
////abstract class AppDataBase :RoomDatabase(){
////    abstract fun productsDao(): ProductsDao
////    abstract fun categoryDao(): CategoryDao
////}