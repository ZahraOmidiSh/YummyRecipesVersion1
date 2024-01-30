package com.zahra.yummyrecipes.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zahra.yummyrecipes.data.database.entity.MealPlannerEntity
import com.zahra.yummyrecipes.data.repository.MealRepository
import com.zahra.yummyrecipes.models.detail.ResponseDetail
import com.zahra.yummyrecipes.utils.NetworkRequest
import com.zahra.yummyrecipes.utils.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MealPlannerViewModel @Inject constructor(
    private val repository: MealRepository,
) : ViewModel() {
    //Call meal api
    val mealData = MutableLiveData<NetworkRequest<ResponseDetail>>()
    fun callMealApi(id: Int, apikey: String) = viewModelScope.launch {
        mealData.value = NetworkRequest.Loading()
        val response = repository.remote.getDetail(id, apikey, true)
        mealData.value = NetworkResponse(response).generalNetworkResponse()
    }

    var theEntity = MutableLiveData<MealPlannerEntity>()

    //save
    fun saveMeal(data: ResponseDetail, date: String) = viewModelScope.launch {
        val newId = (date + data.id).toLong()
        val entity = MealPlannerEntity(newId, data)
        theEntity.value = entity
        repository.local.savePlannedMeal(entity)
    }

    fun deleteMeal(entity: MealPlannerEntity) = viewModelScope.launch {
//        theEntity.value=entity
        repository.local.deletePlannedMeal(entity)
    }


    //format dates
    fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        return dateFormat.format(date)
    }
    private fun formatDateWithMonthDay(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(date)
    }

    //Dates Of Week
    var datesOfWeek = MutableLiveData<List<Date>>()
    fun setDatesOfWeek(day: Date) {
        //make a list od dates
        val dates = mutableListOf<Date>()
        //make an instance of Calendar
        val calendar = Calendar.getInstance()
        calendar.time = day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        //set first day of week
        calendar.firstDayOfWeek = Calendar.SUNDAY

        //sunday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val sunday = calendar.time
        dates.add(sunday)

        //monday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val monday = calendar.time
        dates.add(monday)

        //tuesday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
        val tuesday = calendar.time
        dates.add(tuesday)

        //wednesday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
        val wednesday = calendar.time
        dates.add(wednesday)

        //thursday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
        val thursday = calendar.time
        dates.add(thursday)

        //friday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        val friday = calendar.time
        dates.add(friday)

        //saturday
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        val saturday = calendar.time
        dates.add(saturday)

        datesOfWeek.postValue(dates)
        setWeekTitle()
    }

    val dateList = mutableListOf<String>()
    fun updateDateList(datesOfWeek: List<Date>) {
        dateList.clear()
        datesOfWeek.forEach {
            dateList.add(formatDate(it))
        }
    }

    val dateStringList = mutableListOf<String>()
    fun updateDateStringList(datesOfWeek: List<Date>) {
        dateStringList.clear()
        datesOfWeek.forEach {
            dateStringList.add(formatDateWithMonthDay(it))
        }
    }

    //move week
    var currentDate = Date()
    fun moveWeek(direction: Int) {
        Log.e("dayOfWeek_currentDate1", currentDate.toString() )
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, direction)
        currentDate=calendar.time
        Log.e("dayOfWeek_currentDate2", currentDate.toString() )
        setDatesOfWeek(currentDate)
    }

    //set week title
    val today=Date()
    var weekText = "THIS WEEK"
    fun setWeekTitle(){
        val differenceInDays =
            formatDateWithMonthDay(currentDate).toInt() - formatDateWithMonthDay(today).toInt()
        Log.e("dayOfWeek_differenceInDays_1", differenceInDays.toString() )


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDate(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(dateString, formatter)
    }


    var meals = emptyList<MealPlannerEntity>()
    /*

        fun readMealsOfEachDay(day: String): LiveData<List<MealPlannerEntity>> {
            var mealsForDayList = repository.local.loadPlannedMeals(dateStringList[6]).asLiveData()
            if (day == "sunday") {
                mealsForDayList = repository.local.loadPlannedMeals(dateStringList[0]).asLiveData()
            }
            if (day == "monday") {
                mealsForDayList = repository.local.loadPlannedMeals(dateStringList[1]).asLiveData()
            }
            if (day == "tuesday") {
                mealsForDayList = repository.local.loadPlannedMeals(dateStringList[2]).asLiveData()
            }
            if (day == "wednesday") {
                mealsForDayList = repository.local.loadPlannedMeals(dateStringList[3]).asLiveData()
            }
            if (day == "thursday") {
                mealsForDayList = repository.local.loadPlannedMeals(dateStringList[4]).asLiveData()
            }
            if (day == "friday") {
                mealsForDayList = repository.local.loadPlannedMeals(dateStringList[5]).asLiveData()
            }
            if (day == "saturday") {
                mealsForDayList = repository.local.loadPlannedMeals(dateStringList[6]).asLiveData()
            }
            Log.e("duplicate1", mealsForDayList.value.toString())
            return mealsForDayList
        }
    */

    var recipeId = MutableLiveData<Int>()



//    private fun setWeekTitle(today: Date, currentDay: Date) {
//        val todayStartOfWeek = getStartOfWeek(today).toInt()
//        val currentStartOfWeek = getStartOfWeek(currentDay).toInt()
//        val differenceInDays = currentStartOfWeek - todayStartOfWeek
//
//        weekText = when (differenceInDays) {
//            0 -> "THIS WEEK"
//            -7 -> "LAST WEEK"
//            7 -> "NEXT WEEK"
//            in -8878..-8874 -> "LAST WEEK"
////            else -> "${dateList[0]} - ${dateList[6]}"
//        }
//
//    }

    private fun getStartOfWeek(inputDate: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = inputDate
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        // Optional: If you want to set the time to midnight (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getStartOfWeekDate(inputDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = inputDate
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        // Optional: If you want to set the time to midnight (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.time
    }

//    fun moveOneWeek(direction: Int) {
//        calendar.time = theDay
//        calendar.add(Calendar.DAY_OF_YEAR, direction)
//        calendar.firstDayOfWeek = Calendar.SUNDAY
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
//        theDay = calendar.time
//        updateDatesOfWeekDays()
//    }

//    fun goToCurrentWeek() {
//        calendar.time = Date()
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
//        theDay = calendar.time
//        updateDatesOfWeekDays()
//    }

    fun isTheDatePassed(date: String): Boolean {
        val today = Calendar.getInstance()
        return date < formatDateWithMonthDay(today.time)
    }
}