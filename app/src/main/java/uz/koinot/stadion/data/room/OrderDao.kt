package uz.koinot.stadion.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.OrderHistory
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.model.StadiumOrder

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setAllOrder(list: List<Order>)

    @Query("select * from `Order` where stadiumId=:id order by id desc")
    fun getAllOrders(id: Long): Flow<List<Order>>

    @Query("delete from `Order`")
    fun removeAllOrders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAllStadium(list: List<Stadium>)

    @Query("select * from `Stadium`")
    fun getAllStadiums(): Flow<List<Stadium>>

    @Query("delete from `Stadium`")
    suspend fun removeAllStadiums()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setLikeStadium(stadiumOrder: StadiumOrder)

    @Query("DELETE from StadiumOrder where id = :stadiumId")
    fun unLikeStadium(stadiumId: Int)

    @Query("select * from StadiumOrder where id = :stadiumId")
    fun getStadiumLikeById(stadiumId: Int):StadiumOrder?

    @Query("select * from `StadiumOrder`")
    fun getAllLikeStadiums(): Flow<List<StadiumOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setUserOrder(orderHistory: OrderHistory)

    @Query("select * from `OrderHistory`")
    fun getUserOrder(): Flow<List<OrderHistory>>
}