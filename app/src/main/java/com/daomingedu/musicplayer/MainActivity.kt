package com.daomingedu.musicplayer

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.daomingedu.musicplayer.adapter.LocalMusicAdapter
import com.daomingedu.musicplayer.bean.Music
import com.daomingedu.musicplayer.db.CategoryBean
import com.daomingedu.musicplayer.db.LocalMusic
import com.daomingedu.musicplayer.db.PlayingMusic
import com.daomingedu.musicplayer.service.MusicService
import com.daomingedu.musicplayer.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.litepal.LitePal
import java.io.File


class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val GET_GRADE = "get_grade";
    }

    lateinit var saveBinder: MusicService.MusicServiceBinder

    lateinit var localMusicList: MutableList<LocalMusic>

    private val mAdapter: LocalMusicAdapter = LocalMusicAdapter(mutableListOf())


    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            saveBinder = service as MusicService.MusicServiceBinder


            saveBinder.registerOnStateChangeListener(listener)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            saveBinder.unregisterOnStateChangeListener(listener)
        }

    }

    val listener: MusicService.OnStateChangeListenr = object : MusicService.OnStateChangeListenr {
        override fun onPlayProgressChange(played: Long, duration: Long) {
            MusicTime.text = "${Utils.formatTime(played)}/${Utils.formatTime(duration)}"
        }

        override fun onPlay(item: Music) {
            MusicStatus.text = item.title
            BtnPlayorPause.text ="暂停"

        }

        override fun onPause() {
            BtnPlayorPause.text="播放"
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val categoryName = intent.getStringExtra(GET_GRADE)

        localMusicList =
            LitePal.where("categoryName = '$categoryName'").order("number asc").find(LocalMusic::class.java)


        recycleView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycleView.adapter = mAdapter.apply {

            setOnItemClickListener { adapter, view, position ->
                (adapter.data[position] as LocalMusic).apply {

                    val music = Music(this.songUrl,this.title,this.categoryName,this.number)
                    saveBinder.addPlayList(music)
                }


            }
        }

        mAdapter.setNewInstance(localMusicList)


        //绑定播放服务
        val intent = Intent(this@MainActivity, MusicService::class.java)
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE)




//        从数据库获取保存的本地音乐列表
        val list = LitePal.findAll(LocalMusic::class.java)
        for (localMusic in list) {
            Log.e(this.toString(), localMusic.title)
            Log.e(this.toString(), localMusic.songUrl)
            Log.e(this.toString(), File(Uri.parse(localMusic.songUrl).path!!).exists().toString())
            Log.e(this.toString(), localMusic.number.toString())
        }
//        val plist =LitePal.findAll(PlayingMusic::class.java)
//        for (playingMusic in plist) {
//            Log.e(this.toString(), playingMusic.title)
//            Log.e(this.toString(), playingMusic.songUrl)
//            Log.e(this.toString(), File(playingMusic.songUrl).exists().toString())
//
//        }


    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.BtnPlayorPause -> {
                saveBinder.playOrPause()

            }
            R.id.btnPre -> {
                saveBinder.playPre()

            }
            R.id.btnNext -> {
                saveBinder.playNext()
            }
            R.id.imageView->{
                finish()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
    }


}