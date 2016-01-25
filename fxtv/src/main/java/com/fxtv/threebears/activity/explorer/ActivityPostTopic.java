package com.fxtv.threebears.activity.explorer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fxtv.framework.FrameworkUtils;
import com.fxtv.framework.Logger;
import com.fxtv.framework.frame.BaseFragmentActivity;
import com.fxtv.framework.frame.BaseListGridAdapter;
import com.fxtv.framework.model.ApiType;
import com.fxtv.framework.model.ModuleType;
import com.fxtv.framework.system.SystemUpload;
import com.fxtv.framework.system.components.UploadComponent;
import com.fxtv.threebears.R;
import com.fxtv.threebears.activity.user.userinfo.ActivityPersonalInformation;
import com.fxtv.threebears.model.TopicInfo;
import com.fxtv.threebears.model.TopicMessage;
import com.fxtv.threebears.system.SystemCommon;
import com.fxtv.threebears.system.SystemConfig;
import com.fxtv.threebears.util.Utils;
import com.fxtv.threebears.view.MyDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import emojicon.EmojiconEditText;
import emojicon.EmojiconGridFragment;
import emojicon.EmojiconsFragment;
import emojicon.emoji.Emojicon;

public class ActivityPostTopic extends BaseFragmentActivity implements View.OnClickListener,EmojiconGridFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener{

    private TopicInfo topicinfo;
    private LinearLayout line_emoji;
    private EmojiconsFragment fragment;
    private EmojiconEditText emojicon_edit;
    PopupWindow mImagePopupWindow;
    private int TAKE_FROM_PHOTO = 1994;
    private int GET_IMAGE_FROME_PHOTO = 1995;
    PopupOnclick popupOnclick;
    private String topId;
    private GridView imgs_gridview;
    private CopyOnWriteArrayList<byte[]> listBitmapBytes=new CopyOnWriteArrayList<>();//上传到服务器byte流
    private ExecutorService executors;
    private final int MAX_COUNT=9;//最多为9张图
    private ImgsAdapter imgsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topicinfo= (TopicInfo) getSerializable("topicInfo");
        topId=getStringExtra("topId");
        Logger.d("TAG",""+topicinfo);
        setContentView(R.layout.activity_post_topic);
        initView();
    }

    private void initView() {
        initBar();
        imgs_gridview=(GridView)findViewById(R.id.imgs_gridview);
        line_emoji=(LinearLayout)findViewById(R.id.line_emoji);
        emojicon_edit=(EmojiconEditText)findViewById(R.id.emojicon_edit);

        findViewById(R.id.im_emoji).setOnClickListener(this);
        findViewById(R.id.im_select_img).setOnClickListener(this);
        emojicon_edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                line_emoji.setVisibility(View.VISIBLE);
                initEmojiFragment();
                return false;
            }
        });
        ArrayList<Bitmap> list=new ArrayList<>();
        list.add(BitmapFactory.decodeResource(getResources(),R.drawable.post_img_add));
        imgsAdapter=new ImgsAdapter(list);

        imgs_gridview.setAdapter(imgsAdapter);
    }

    private void initBar() {
        if(topicinfo!=null)
            ((TextView)findViewById(R.id.ab_title)).setText(""+topicinfo.title);
        ((TextView)findViewById(R.id.ab_title)).setMaxLines(1);
        findViewById(R.id.ab_left_img).setVisibility(View.GONE);
        TextView ab_left_tv = (TextView) findViewById(R.id.ab_left_tv);
        ab_left_tv.setVisibility(View.VISIBLE);
        ab_left_tv.setText("取消");
        ab_left_tv.getLayoutParams().width=FrameworkUtils.dip2px(this,50);

        ab_left_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView ab_right_tv=(TextView)findViewById(R.id.ab_right_tv);
        ab_right_tv.setText("发送");
        ab_right_tv.setVisibility(View.VISIBLE);
        ab_right_tv.setOnClickListener(this);
        findViewById(R.id.ab_left_img).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ab_right_tv://发送
                String content=emojicon_edit.getText().toString();
                if((TextUtils.isEmpty(content) || TextUtils.isEmpty(content.trim())) && (listBitmapBytes==null || listBitmapBytes.size()<=0)){
                    showToast("吐槽点内容吧！");
                    return;
                }
                Utils.showProgressDialog(this);

                JsonObject json=new JsonObject();
                json.addProperty("id",""+topId);//吐槽id
                json.addProperty("content", FrameworkUtils.string2Unicode(content));//评论内容

                String url = Utils.processUrlForPhoto(ModuleType.USER, ApiType.USER_topicMessage,json);

                new UploadComponent().upload(url, listBitmapBytes, new SystemUpload.IUploadCallBack() {

                    @Override
                    public void onSuccess(String imageUrl,String msg) {
                        Utils.dismissProgressDialog();
                        Logger.d("TAG", "onSuccess=" + imageUrl);
                        TopicMessage topicMessage=new Gson().fromJson(imageUrl, TopicMessage.class);
                        showToast(""+msg);
                        Intent intent=new Intent();
                        intent.putExtra("TopicMessage",topicMessage);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e,String msg) {
                        showToast(""+msg);
                        Utils.dismissProgressDialog();
                    }

                });
                break;
            case R.id.im_emoji:
                initEmojiFragment();
                break;
            case R.id.im_select_img:
                Utils.setInputGone(this);
                if(!isAdd()) return;
                if(popupOnclick ==null){ popupOnclick =new PopupOnclick();}
                showImagePop(v);
                break;
            case R.id.ab_left_img://退出
                onBackPressed();
                break;
        }
    }
    private boolean isAdd(){
        if(imgsAdapter.getListData()!=null && imgsAdapter.getCount()>MAX_COUNT){
            showToast("最多选取9张图");
            return false;
        }
        return true;
    }
    private void initEmojiFragment(){
        if(fragment==null){
            fragment=new EmojiconsFragment();
            getSupportFragmentManager().beginTransaction().add(line_emoji.getId(),fragment).show(fragment).commit();
        }
        if(line_emoji.getVisibility()==View.VISIBLE){
            line_emoji.setVisibility(View.GONE);
            Utils.setInputVisible(this, emojicon_edit);
        }else{
            Utils.setInputGone(this);
            line_emoji.setVisibility(View.VISIBLE);
        }


    }
    // 选择头像的方式
    private void showImagePop(View parent) {
        if (mImagePopupWindow != null && !mImagePopupWindow.isShowing()) {
            mImagePopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        } else {
            ViewGroup layout = (ViewGroup) View.inflate(this, R.layout.pop_image, null);
            mImagePopupWindow = new PopupWindow(layout, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT, true);
            mImagePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.touming_black_color));
            layout.findViewById(R.id.img).setVisibility(View.GONE);
            // 相册照片
            ((TextView)layout.findViewById(R.id.take_sys_photo)).setText("从相册选择");
            layout.findViewById(R.id.take_sys_photo).setOnClickListener(popupOnclick);
            // 拍照
            layout.findViewById(R.id.take_photo).setOnClickListener(popupOnclick);
            layout.findViewById(R.id.take_from_photo).setVisibility(View.GONE);
            // 取消
            layout.findViewById(R.id.cancel).setOnClickListener(popupOnclick);
            mImagePopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }
    class PopupOnclick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.take_photo:
                    if(!isAdd()) return;
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){//SD存在

                        File file=new File(ActivityPersonalInformation.path);
                        if(!file.exists()){
                            boolean mkdirs=file.mkdirs();
                            Logger.d("TAG", "/files/images不存在，创建？" + mkdirs);
                        }
                        File fileTemp=new File(file.getPath()+"/temp.jpg");//拍摄原图，如果没有,取到的不是高清图
                        if(!fileTemp.exists()) try {
                            fileTemp.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Logger.d("TAG", "temp.jpg存在？" + fileTemp.exists());

                        Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileTemp));
                        startActivityForResult(intent, TAKE_FROM_PHOTO);
                    }else{
                        Intent intent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, TAKE_FROM_PHOTO);
                    }
                    break;
                case R.id.take_sys_photo:
                    if(!isAdd()) return;
                    Intent intentFromGallery = new Intent();
                    intentFromGallery.setType("image/*");
                    intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intentFromGallery, GET_IMAGE_FROME_PHOTO);
                    break;
                case R.id.cancel:
                    mImagePopupWindow.dismiss();
                    break;

            }
        }
    }
    class ImgsAdapter extends BaseListGridAdapter<Bitmap>{
        public ImgsAdapter(List<Bitmap> list){
            super(list);
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder;
            if(convertView==null){
                holder=new Holder();
                convertView=mLayoutInflater.inflate(R.layout.view_img_item,parent,false);
                holder.im_item= (ImageView) convertView.findViewById(R.id.im_item);
                holder.im_delete= (ImageView) convertView.findViewById(R.id.im_delete);
                convertView.setTag(holder);
            }else{
                holder= (Holder) convertView.getTag();
            }

            holder.im_item.setImageBitmap(getItem(position));
            if(position==getCount()-1){
                holder.im_delete.setVisibility(View.GONE);
                convertView.setId(R.id.im_select_img);
                convertView.setOnClickListener(ActivityPostTopic.this);
                if(position==MAX_COUNT || getCount()<=1){//图片等于9张或者小于1张（包括Add图片的图片），隐藏+
                    convertView.setVisibility(View.GONE);
                }
            }else{
                holder.im_delete.setVisibility(View.VISIBLE);
                convertView.setOnClickListener(null);
            }
            holder.im_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSystem(SystemCommon.class)
                            .showDialog(ActivityPostTopic.this, "提示", "要删除这张照片吗？", new MyDialog.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog, View view, String value) {
                                    dialog.dismiss();
                                    getListData().remove(position).recycle();
                                    listBitmapBytes.remove(position);
                                    notifyDataSetChanged();
                                }
                            }, new MyDialog.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog, View view, String value) {
                                    dialog.dismiss();
                                }
                            });

                }
            });
            return convertView;
        }
        class Holder{
            ImageView im_item;
            ImageView im_delete;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode,resultCode,data);
        Bitmap bitmap=null;
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_FROM_PHOTO) {//拍照
                try {
                File file_temp = new File(ActivityPersonalInformation.path + "/temp.jpg");
                //从原图中读取
                if (file_temp.exists()) {
                    Logger.d("TAG", "从原图中读取");
                    //file_temp.getAbsolutePath() 就是拍摄获得的高清图片的path
                    bitmap=Utils.getBitmapToPath(file_temp.getAbsolutePath(), SystemConfig.POST_IMG_MAX_W, SystemConfig.POST_IMG_MAX_H);
                }
                if (bitmap == null && data != null) {//双重判断，无论如何也不会为空
                    //原图不存在，取模糊图
                    Logger.d("TAG", "原图不存在,取模糊图");
                    Uri uri = data.getData();
                    if (uri == null) {//有的机型会为null
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            bitmap = (Bitmap) bundle.get("data");
                        }
                    } else {
                        bitmap = Utils.getBitmapToUrl(this,uri, SystemConfig.POST_IMG_MAX_W, SystemConfig.POST_IMG_MAX_H);
                    }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.d("TAG","拍照异常"+e.getMessage());
                }
            } else if (requestCode == GET_IMAGE_FROME_PHOTO && data.getData()!=null) {//相册
                bitmap=Utils.getBitmapToUrl(this,data.getData(), SystemConfig.POST_IMG_MAX_W, SystemConfig.POST_IMG_MAX_H);
            }
            Logger.d("TAG", "bitmap==null?" + (bitmap==null));
            if(bitmap!=null){
                Logger.d("TAG", "" + bitmap.getWidth() + "*" + bitmap.getHeight() + "=" + bitmap.getByteCount());

                if(imgsAdapter.getListData()==null){
                    imgsAdapter.setListData(new ArrayList<Bitmap>());
                }
                int index=imgsAdapter.getCount()==0?0:imgsAdapter.getCount()-1;
                imgsAdapter.getListData().add(index,bitmap);//添加到倒数第二位
                imgsAdapter.notifyDataSetChanged();
                //img.setOnClickListener(new ImgClick(bitmaps.size()-1));

                if(executors==null){
                    executors=Executors.newFixedThreadPool(5);
                }
                final Bitmap bitmaptEmp=bitmap;
                executors.execute(new Runnable() {
                    @Override
                    public void run() {
                        Logger.d("TAG","run bitmapToByteArray？"+listBitmapBytes.size());
                        listBitmapBytes.add(Utils.bitmapToByteArray(bitmaptEmp));
                    }
                });
            }else{
                showToast("无法读取图片");
            }

            if(imgsAdapter.getCount()>=1){
                imgs_gridview.setVisibility(View.VISIBLE);
            }
        }
    }
    class ImgClick implements View.OnClickListener{
        private int position;

        public ImgClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ActivityPostTopic.this,  ActivityExplorerImagePager.class);
            //intent.putParcelableArrayListExtra("Bitmaps", bitmaps); intent传递对象最大40K,所以此处跳转无效
            intent.putExtra("postion", position);
            startActivity(intent);

        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View arg0) {
        // TODO Auto-generated method stub
        EmojiconsFragment.backspace(emojicon_edit);
    }
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        // TODO Auto-generated method stub
        EmojiconsFragment.input(emojicon_edit, emojicon);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listBitmapBytes=null;
        if(imgsAdapter!=null && imgsAdapter.getListData()!=null){
            for (Bitmap bitmap:imgsAdapter.getListData()) {
                bitmap.recycle();
            }
        }
        imgsAdapter=null;
        executors=null;
    }

    @Override
    public void onBackPressed() {
        if(!TextUtils.isEmpty(emojicon_edit.getText().toString()) || imgsAdapter.getCount()>1){
            getSystem(SystemCommon.class)
                    .showDialog(ActivityPostTopic.this, "退出此次编辑？", null,new MyDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, View view, String value) {
                            dialog.dismiss();
                            finish();
                        }
                    }, new MyDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog, View view, String value) {
                            dialog.dismiss();
                        }
                    });
        }else{
            super.onBackPressed();
        }
    }
}
