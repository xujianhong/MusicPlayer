package com.daomingedu.musicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.daomingedu.musicplayer.adapter.GradeAdapter
import com.daomingedu.musicplayer.bean.Music
import com.daomingedu.musicplayer.db.CategoryBean
import com.daomingedu.musicplayer.db.LocalMusic
import com.daomingedu.musicplayer.db.PlayingMusic
import kotlinx.android.synthetic.main.activity_grade.*
import org.litepal.LitePal


class GradeActivity : AppCompatActivity(), View.OnClickListener {

    companion object {

        const val REQUEST_CODE: Int = 1024

    }

    lateinit var updateTask: MusicUpdateTask

    private var mAdapter: GradeAdapter = GradeAdapter(mutableListOf())

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade)



        progressDialog = ProgressDialog(this)

        recycleView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycleView.adapter = mAdapter.apply {

            setOnItemClickListener { adapter, view, position ->
                startActivity(
                    Intent(this@GradeActivity, MainActivity::class.java).putExtra(
                        MainActivity.GET_GRADE,
                        (adapter.data[position] as CategoryBean).categoryName
                    )
                )
            }

        }

        //从数据库获取保存的本地音乐列表
        val cList = LitePal.findAll(CategoryBean::class.java)
        for (categoryBean in cList) {
            Log.e(this.toString(), categoryBean.categoryName)
        }

        mAdapter.setNewInstance(cList)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button -> {
                mAdapter.data.clear()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 先判断有没有权限
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        updateTask = MusicUpdateTask(this, mAdapter, progressDialog)
                        updateTask.execute()
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            REQUEST_CODE
                        )
                    }
                } else {
                    updateTask = MusicUpdateTask(this, mAdapter, progressDialog)
                    updateTask.execute()
                }


            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                updateTask = MusicUpdateTask(this, mAdapter, progressDialog)
                updateTask.execute()
            } else {
                Toast.makeText(this, "权限获取失败！", Toast.LENGTH_LONG).show()
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    class MusicUpdateTask(
        private val context: Context,
        private val mAdapter: GradeAdapter,
        private val progressDialog: ProgressDialog
    ) : AsyncTask<Any, Music, Any>() {

        //临时分类集合
        private val _categoryList: MutableList<String> = mutableListOf()

        override fun onPreExecute() {
            progressDialog.setMessage("获取本地音乐中...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg params: Any?) {

            LitePal.deleteAll(CategoryBean::class.java) //清空数据库
            LitePal.deleteAll(LocalMusic::class.java) //清空数据库
            LitePal.deleteAll(PlayingMusic::class.java)

            val searchKey = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
            )

            val path = arrayOf(
                Environment.getExternalStorageDirectory().absolutePath + "/Download%"
            )
            val selection = MediaStore.Images.Media.DATA + " like ?"
            val resolver: ContentResolver = context.contentResolver
            val cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                searchKey,
                selection,
                path,
                null
            )
            if (cursor != null) {
                while (cursor.moveToNext() && !isCancelled) {
                    val id =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    //通过URI和ID，组合出改音乐特有的Uri地址
                    val musicUri =
                        Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val title =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))


                    val str: Array<String> = title.split("@").toTypedArray()
                    if (str.size == 2) {
                        val str2: Array<String> = str[1].split("$").toTypedArray()
                        if (str2.size == 2) {
                            Log.e("获取音频资源", "$title     ${str2[1]}")
                            val data =
                                Music(path, str2[0], str[0], str2[1].toInt())
                            //切换到主线程进行更新
                            publishProgress(data)
                        }
                    }
                }
                cursor.close()
            }
            return
        }

        override fun onProgressUpdate(vararg values: Music) {

            val data: Music = values[0]
            val cate = CategoryBean(data.categoryName)


            if (!_categoryList.contains(cate.categoryName)) {
                cate.save()
                _categoryList.add(cate.categoryName)

                mAdapter.data.add(cate)
                mAdapter.notifyDataSetChanged()
            }
            val localMusic = LocalMusic(data.songUrl, data.title, data.categoryName,data.number)
            localMusic.save()

        }


        override fun onPostExecute(result: Any?) {
            progressDialog.dismiss()
        }

    }


}