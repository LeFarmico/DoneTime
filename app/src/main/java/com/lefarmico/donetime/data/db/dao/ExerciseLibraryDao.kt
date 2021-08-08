package com.lefarmico.donetime.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lefarmico.donetime.data.db.entities.LibraryCategory
import com.lefarmico.donetime.data.db.entities.LibraryExercise
import com.lefarmico.donetime.data.db.entities.LibrarySubCategory
import io.reactivex.rxjava3.core.Observable

@Dao
interface ExerciseLibraryDao {

    @Query("SELECT * FROM exercise_library_type")
    fun getCategories(): Observable<List<LibraryCategory>>

    @Query("SELECT * FROM exercise_library_sub_category WHERE category_id = :id")
    fun getSubCategoriesByCategoryId(id: Int): Observable<LibrarySubCategory>

    @Query("SELECT * FROM exercise_library_exercise WHERE sub_category_id = :id")
    fun getExercisesBySubCategoryId(id: Int): Observable<LibraryExercise>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCategory(category: LibraryCategory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSubCategory(subCategory: LibrarySubCategory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertExercise(exercise: LibraryExercise)
}
