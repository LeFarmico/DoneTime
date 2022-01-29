package com.lefarmico.domain.repository

import com.lefarmico.domain.entity.LibraryDto
import com.lefarmico.domain.utils.DataState
import io.reactivex.rxjava3.core.Single

interface LibraryRepository : BaseRepository {

    fun getCategories(): Single<DataState<List<LibraryDto.Category>>>

    fun getCategory(categoryId: Int): Single<DataState<LibraryDto.Category>>

    fun getSubCategories(categoryId: Int): Single<DataState<List<LibraryDto.SubCategory>>>

    fun getSubCategory(subcategoryId: Int): Single<DataState<LibraryDto.SubCategory>>

    fun getExercises(subCategoryId: Int): Single<DataState<List<LibraryDto.Exercise>>>

    fun getExercise(exerciseId: Int): Single<DataState<LibraryDto.Exercise>>

    fun addCategory(category: LibraryDto.Category): Single<DataState<Long>>

    fun addSubCategory(subCategory: LibraryDto.SubCategory): Single<DataState<Long>>

    fun addSubCategories(subcategoryList: List<LibraryDto.SubCategory>): Single<DataState<List<Long>>>

    fun addExercise(exercise: LibraryDto.Exercise): Single<DataState<Long>>

    fun deleteCategory(categoryId: Int): Single<DataState<Int>>

    fun deleteSubcategory(subCategoryId: Int): Single<DataState<Int>>

    fun deleteExercise(exerciseId: Int): Single<DataState<Int>>
}
