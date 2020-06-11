package cz.covid19cz.erouska.db

import cz.covid19cz.erouska.AppConfig
import cz.covid19cz.erouska.ext.daysToMilis
import io.reactivex.Flowable
import io.reactivex.Single

interface DatabaseRepository {
    fun getAll(): Single<List<ScanDataEntity>>
    fun getAllDesc(): Flowable<List<ScanDataEntity>>
    fun getCriticalDesc(): Flowable<List<ScanDataEntity>>
    fun getAllFromTimestamp(timestamp: Long): Single<List<ScanDataEntity>>
    fun add(scanData: ScanDataEntity): Long
    fun getTuidCount(since: Long): Flowable<Int>
    fun getCriticalTuidCount(since: Long): Flowable<Int>
    fun delete(scanData: ScanDataEntity)
    fun deleteOldData() : Int
    fun clear()
}

class ExpositionRepositoryImpl(private val dao: ScanDataDao) :
    DatabaseRepository {

    override fun getAll(): Single<List<ScanDataEntity>> {
        return dao.getAll()
    }

    override fun getAllDesc() : Flowable<List<ScanDataEntity>>{
        return dao.getAllDesc()
    }

    override fun getCriticalDesc() : Flowable<List<ScanDataEntity>>{
        return dao.getCriticalDesc(AppConfig.criticalExpositionRssi)
    }

    override fun add(device: ScanDataEntity): Long {
        if (device.tuid.length == 20) {
            return dao.insert(device)
        }
        return 0L
    }

    override fun getAllFromTimestamp(timestamp: Long): Single<List<ScanDataEntity>> {
        return dao.getAllFromTimestamp(timestamp)
    }

    override fun getCriticalTuidCount(since: Long): Flowable<Int> {
        return dao.getTuidCount(since, AppConfig.criticalExpositionRssi)
    }

    override fun getTuidCount(since: Long): Flowable<Int> {
        return dao.getTuidCount(since, -150)
    }

    override fun delete(scanData: ScanDataEntity) {
        dao.delete(scanData)
    }

    override fun deleteOldData(): Int {
        return dao.deleteOldData(System.currentTimeMillis() - AppConfig.persistDataDays.daysToMilis())
    }

    override fun clear() {
        dao.clear()
    }
}