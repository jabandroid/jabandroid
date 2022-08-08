package com.global.vtg.appview.home.event

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.global.vtg.appview.authentication.UserRepository
import com.global.vtg.appview.authentication.registration.ResUser
import com.global.vtg.base.AppViewModel
import com.global.vtg.model.network.Resource
import com.global.vtg.model.network.result.BaseResult
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ContactListViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AppViewModel(application) {


    val searchUser = MutableLiveData<Resource<ResUser>>()

    private val searUserLiveDataObserver = Observer<Resource<ResUser>> {
        searchUser.postValue(it)
    }

    val addUser = MutableLiveData<Resource<BaseResult>>()

    private val addUserLiveData = Observer<Resource<BaseResult>> {
        addUser.postValue(it)
    }

    val getEventUser = MutableLiveData<Resource<Attendees>>()

    private val getEventUsers = Observer<Resource<Attendees>> {
        getEventUser.postValue(it)
    }

    val deleteUser = MutableLiveData<Resource<BaseResult>>()

    private val deleteUSerLiveData = Observer<Resource<BaseResult>> {
        deleteUser.postValue(it)
    }
    init {
        userRepository.searchUser.postValue(null)
        userRepository.searchUser.observeForever(searUserLiveDataObserver)
        userRepository.addUserLiveData.postValue(null)
        userRepository.addUserLiveData.observeForever(addUserLiveData)
        userRepository.getEventsUSerLiveData.postValue(null)
        userRepository.getEventsUSerLiveData.observeForever(getEventUsers)
        userRepository.deleteUserLiveData.postValue(null)
        userRepository.deleteUserLiveData.observeForever(deleteUSerLiveData)
    }

    val contactLiveDat = MutableLiveData<ArrayList<ContactItem>>()
    @SuppressLint("Range")
    fun getAllContact(c: Context){

        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {


            val arrListContact = ArrayList<ContactItem>()

            val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val cr: ContentResolver = c.contentResolver
            val cursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
            )
            var count = 0
            if (cursor != null) {
                try {
                    val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val numberIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val photoIndex =
                        cursor.getColumnIndex( ContactsContract.Contacts.PHOTO_URI)

                    var name: String?
                    var number: String?
                    while (cursor.moveToNext()) {
                        val contact = ContactItem()
                        name = cursor.getString(nameIndex)
                        number = cursor.getString(numberIndex)
                        val photo = cursor.getString(photoIndex)
                        contact.setNumber(number)
                        contact.setName(name)
                        contact.setId(count)
                        contact.setPhoto(photo)
                        count++
                        arrListContact.add(contact)
                    }
                } finally {
                    cursor.close()
                }
            }

//            val PROJECTION = arrayOf(
//                ContactsContract.Contacts._ID,
//                ContactsContract.Contacts.DISPLAY_NAME,
//                ContactsContract.CommonDataKinds.Phone.NUMBER
//            )
//            val cr: ContentResolver = c.contentResolver
//            val cur: Cursor? = cr.query(
//                ContactsContract.Contacts.CONTENT_URI,
//                null, null, null, null
//            )
//
//            var count = 0
//            if ((cur?.count ?: 0) > 0) {
//                while (cur != null && cur.moveToNext()) {
//                    val contact = ContactItem()
//                    val id: String = cur.getString(
//                        cur.getColumnIndex(ContactsContract.Contacts._ID)
//                    )
//
//                    contact.setId(count)
//                    count++
//                    val name: String = cur.getString(
//                        cur.getColumnIndex(
//                            ContactsContract.Contacts.DISPLAY_NAME
//                        )
//                    )
//
//                    if (!TextUtils.isEmpty(name))
//                        contact.setName(name)
//                    if (cur.getInt(
//                            cur.getColumnIndex(
//                                ContactsContract.Contacts.HAS_PHONE_NUMBER
//                            )
//                        ) > 0
//                    ) {
//                        val pCur: Cursor? = cr.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                            arrayOf(id),
//                            null
//                        )
//                        while (pCur!!.moveToNext()) {
//                            val phoneNo: String = pCur.getString(
//                                pCur.getColumnIndex(
//                                    ContactsContract.CommonDataKinds.Phone.NUMBER
//                                )
//                            )
//                            if (!TextUtils.isEmpty(phoneNo))
//                                contact.setNumber(phoneNo)
//
//                        }
//                        pCur.close()
//                    }
//
////                    val photo: String = cur.getString(
////                        cur.getColumnIndex(
////                            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
////                        )
////                    )
//
//
//                   // contact.setPhoto(getPhotoUri(c, id).toString())
//
//                    arrListContact.add(contact)
//                }
//            }
//            cur?.close()
            contactLiveDat.postValue(arrListContact)
            handler.post(Runnable {
                //UI Thread work here
            })
        }

    }

    @SuppressLint("Recycle")
    fun getPhotoUri(c:Context, id:String): Uri? {
        try {
            val cur: Cursor? = c.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                        + ContactsContract.Data.MIMETYPE + "='"
                        + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                null
            )
            if (cur != null) {
                if (!cur.moveToFirst()) {
                    return null // no photo
                }
            } else {
                return null // error in cursor process
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val person: Uri =
            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
    }


    fun searchUser(id:String) {
        scope.launch {
            userRepository.searchUser(id)
        }
    }

    fun addUSer(id:JsonObject) {
        scope.launch {
            userRepository.addUser(id)
        }
    }

    fun getUsers(id:String) {
        scope.launch {
            userRepository.getUsers(id)
        }
    }

    fun deleteUSer(obj:String) {
        scope.launch {
            userRepository.deleteUser(obj)
        }
    }

    override fun onCleared() {
        super.onCleared()
        userRepository.addUserLiveData.removeObserver(addUserLiveData)
        userRepository.getEventsUSerLiveData.removeObserver(getEventUsers)
        userRepository.getEventsUSerLiveData.removeObserver(deleteUSerLiveData)

    }

}

