package com.shels.delivery.DataBaseUtils.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.shels.delivery.Data.Product;

import java.util.List;

@Dao
public interface ProductDao {

    @Query("SELECT * FROM products WHERE documentId = :documentId")
    LiveData<List<Product>> getProductsByDocumentId(String documentId);

    @Query("SELECT * FROM products WHERE documentId = :documentId AND productId = :productId")
    Product getProductByProductDocumentId(String productId, String documentId);

    @Query("SELECT sum(scanned) FROM products WHERE documentId = :documentId")
    int getScanned(String documentId);

    @Query("SELECT sum(amount) FROM products WHERE documentId = :documentId")
    int getAmount(String documentId);

    @Update
    void updateProduct(Product product);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProducts(List<Product> products);

    @Query("DELETE FROM products")
    void deleteAllProducts();

}
