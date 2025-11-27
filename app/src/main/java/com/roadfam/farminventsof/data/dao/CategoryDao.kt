package com.roadfam.farminventsof.data.dao

import androidx.room.*
import com.roadfam.farminventsof.data.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)
    
    @Query("UPDATE categories SET itemCount = (SELECT COUNT(*) FROM inventory_items WHERE categoryId = :categoryId) WHERE id = :categoryId")
    suspend fun updateCategoryItemCount(categoryId: Long)
}

