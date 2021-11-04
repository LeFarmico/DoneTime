package com.lefarmico.data.repository

import com.lefarmico.data.db.dao.LibraryDao
import com.lefarmico.data.extensions.dataStateActionResolver
import com.lefarmico.data.extensions.dataStateResolver
import com.lefarmico.data.mapper.toData
import com.lefarmico.data.mapper.toDto
import com.lefarmico.data.mapper.toDtoCategoryList
import com.lefarmico.data.mapper.toDtoExerciseList
import com.lefarmico.data.mapper.toDtoSubCategoryList
import com.lefarmico.domain.entity.LibraryDto
import com.lefarmico.domain.repository.LibraryRepository
import com.lefarmico.domain.utils.DataState
import io.reactivex.rxjava3.core.Single
import java.lang.Exception
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val dao: LibraryDao
) : LibraryRepository {

    override fun getCategories(): Single<DataState<List<LibraryDto.Category>>> {
        return dao.getCategories()
            .doOnSubscribe { DataState.Loading }
            .doOnError { DataState.Error(it as Exception) }
            .map { data -> dataStateResolver(data.toDtoCategoryList()) }
    }

    override fun getSubCategories(categoryId: Int): Single<DataState<List<LibraryDto.SubCategory>>> {
        return dao.getSubCategories(categoryId)
            .doOnSubscribe { DataState.Loading }
            .doOnError { DataState.Error(it as Exception) }
            .map { data -> dataStateResolver(data.toDtoSubCategoryList()) }
    }

    override fun getExercises(subCategoryId: Int): Single<DataState<List<LibraryDto.Exercise>>> {
        return dao.getExercises(subCategoryId)
            .doOnSubscribe { DataState.Loading }
            .doOnError { DataState.Error(it as Exception) }
            .map { data -> dataStateResolver(data.toDtoExerciseList()) }
    }

    override fun getExercise(exerciseId: Int): Single<DataState<LibraryDto.Exercise>> {
        return dao.getExercise(exerciseId)
            .doOnSubscribe { DataState.Loading }
            .doOnError { DataState.Error(it as Exception) }
            .map { data -> DataState.Success(data.toDto()) }
    }

    override fun addCategory(category: LibraryDto.Category): Single<DataState<Long>> {
        return Single.create<DataState<Long>> { emitter ->
            emitter.onSuccess(
                dataStateActionResolver { dao.insertCategory(category.toData()) }
            )
        }.doOnError { DataState.Error(it as Exception) }
    }

    override fun addSubCategory(subCategory: LibraryDto.SubCategory): Single<DataState<Long>> {
        return Single.create<DataState<Long>> { emitter ->
            emitter.onSuccess(
                dataStateActionResolver { dao.insertSubCategory(subCategory.toData()) }
            )
        }.doOnError { DataState.Error(it as Exception) }
    }

    override fun addExercise(exercise: LibraryDto.Exercise): Single<DataState<Long>> {
        return Single.create<DataState<Long>> { emitter ->
            emitter.onSuccess(
                dataStateActionResolver { dao.insertExercise(exercise.toData()) }
            )
        }.doOnError { DataState.Error(it as Exception) }
    }
}
