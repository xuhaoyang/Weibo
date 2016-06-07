package com.xhy.weibo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import android.view.ViewGroup.LayoutParams;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.EmotionGvAdapter;
import com.xhy.weibo.adapter.EmotionPagerAdapter;
import com.xhy.weibo.adapter.UserAdpater;
import com.xhy.weibo.adapter.WriteStatusGridImgsAdapter;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.entity.Comment;
import com.xhy.weibo.entity.NormalInfo;
import com.xhy.weibo.entity.Picture;
import com.xhy.weibo.entity.PictureReciver;
import com.xhy.weibo.entity.Status;
import com.xhy.weibo.network.GsonRequest;
import com.xhy.weibo.network.ImageUpload;
import com.xhy.weibo.network.NetParams;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.network.VolleyQueueSingleton;
import com.xhy.weibo.utils.DisplayUtils;
import com.xhy.weibo.utils.EmotionUtils;
import com.xhy.weibo.utils.ImageUtils;
import com.xhy.weibo.utils.StringUtils;
import com.xhy.weibo.widget.WrapHeightGridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WriteStatusActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String STATUS_INTENT = "status";
    public static final String COMMENT_INTENT = "comment";
    public static final String SEND_COMMENT_SUCCESS = "SEND_COMMENT_OK";
    public static final String SEND_FORWORD_SUCCESS = "SEND_FORWORD_OK";
    public static final String SEND_STATUS_SUCCESS = "SEND_STATUS_OK";
    //打上TAG标签 判断来自哪个Activity
    public static final String TAG = "TAG";
    public static final int MAIN_ATY_CODE = 1;
    public static final int DETAIL_ATY_CODE = 2;
    public static final int COMMENT_ADPATER_CODE = 3;

    //
    public static final String TYPE = "WRITECOMMENTACTIVITY_TYPE";
    public static final int COMMENT_TYPE = 101;
    public static final int FORWARD_TYPE = 102;
    public static final int NEW_STATUS_TYPE = 103;


    public static final int REQUEST_CODE_AT_USERNAME = 201;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //底部
    @BindView(R.id.new_take_photo)
    ImageButton new_take_photo;
    @BindView(R.id.new_emotion)
    ImageButton new_emotion;
    @BindView(R.id.new_friend)
    ImageButton new_friend;
    @BindView(R.id.new_trend)
    ImageButton new_trend;
    @BindView(R.id.new_send)
    ImageButton new_send;

    @BindView(R.id.new_edit)
    EditText new_edit;

    @BindView(R.id.gv_write_status)
    WrapHeightGridView gv_write_status;

    @BindView(R.id.ll_emotion_dashboard)
    LinearLayout ll_emotion_dashboard;
    @BindView(R.id.vp_emotion_dashboard)
    ViewPager vp_emotion_dashboard;

    //带评论的微博
    private Status status;
    private Comment comment;
    //来自哪个ATY
    private int aty_stype;
    private String original;
    //评论?转发
    private int type;

    //图片类
    private Picture picture = null;

    //发送图片微博时显示用
    private ProgressDialog mDialog;

    private ArrayList<Uri> imgUris = new ArrayList<Uri>();
    private WriteStatusGridImgsAdapter statusImgsAdapter;
    private EmotionPagerAdapter emotionPagerGvAdapter;
    private InputMethodManager inputMethodManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDialog = new ProgressDialog(this);
        mDialog.setCanceledOnTouchOutside(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //默认转发评论都无法发图片
        type = getIntent().getIntExtra(TYPE, 101);
        aty_stype = getIntent().getIntExtra(TAG, 1);
        switch (type) {
            case COMMENT_TYPE:
                //获取待评论微博数据
                switch (aty_stype) {
                    //来自Status 微博的评论[获得是Status的信息]
                    case MAIN_ATY_CODE:
                    case DETAIL_ATY_CODE:
                        status = (Status) getIntent().getSerializableExtra(STATUS_INTENT);
                        getSupportActionBar().setTitle("发表评论");
                        new_take_photo.setVisibility(View.GONE);
                        break;
                    //来自评论列表的回复[获得是Comment信息]
                    case COMMENT_ADPATER_CODE:
                        comment = (Comment) getIntent().getSerializableExtra(COMMENT_INTENT);
                        getSupportActionBar().setTitle("回复评论");
                        original = "@" + comment.getUsername() + ": " + comment.getContent();
                        new_edit.setHint(original);
                        new_take_photo.setVisibility(View.GONE);
                        break;
                }
                break;
            case FORWARD_TYPE:
                new_take_photo.setVisibility(View.GONE);
                status = (Status) getIntent().getSerializableExtra(STATUS_INTENT);
                getSupportActionBar().setTitle("转发微博");
                break;
            case NEW_STATUS_TYPE:
                getSupportActionBar().setTitle("发微博");
            default:
                break;
        }

        statusImgsAdapter = new WriteStatusGridImgsAdapter(this, imgUris, gv_write_status);
        gv_write_status.setAdapter(statusImgsAdapter);
        gv_write_status.setOnItemClickListener(this);

        inputMethodManager = (InputMethodManager) getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        new_edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean bool = inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
                if (bool) {
                    ll_emotion_dashboard.setVisibility(View.GONE);
                }
                return false;
            }
        });
        initEmotion();

        initListener();


    }

    private void initEmotion() {
        int screenWidth = DisplayUtils.getScreenWidthPixels(this);
        int spacing = DisplayUtils.dp2px(this, 8);

        int itemWidth = (screenWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<String> emotionNames = new ArrayList<String>();
        for (String emojiName : EmotionUtils.emojiMap.keySet()) {
            emotionNames.add(emojiName);

            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);

                emotionNames = new ArrayList<String>();
            }
        }

        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

        emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        vp_emotion_dashboard.setLayoutParams(params);
        vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        GridView gv = new GridView(this);
        gv.setBackgroundResource(R.color.bg_gray);
        gv.setSelector(R.color.transparent);
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);

        LayoutParams params = new LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);

        EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);

        return gv;
    }

    private void initListener() {

        new_send.setOnClickListener(this);
        new_trend.setOnClickListener(this);
        new_friend.setOnClickListener(this);
        new_emotion.setOnClickListener(this);
        new_take_photo.setOnClickListener(this);
    }

    private static final int UPLOAD_OK = 10001;

    private Handler imageHandler = new Handler() {
    };

    private void sendImage(final Uri imageUri) {

        mDialog.setMessage("图片上传中...");
        mDialog.show();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        String imageFilePath = ImageUtils.getImageAbsolutePath19(this, imageUri);
        final File imageFile = new File(imageFilePath);
        final String url = NetParams.uploadUserPic(CommonConstants.ACCESS_TOKEN.getToken(), height, width);

        //Volley对稍大文件上传支持的很不好,很容易被OC,故用HttpURLConnection重写的上传
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = ImageUpload.uploadFile(url, imageFile, null);
                Gson gson = new Gson();
                final PictureReciver pictureReciver = gson.fromJson(result, PictureReciver.class);
                if (pictureReciver.getCode() == 200) {
                    //上传成功
                    Message.obtain(imageHandler, new Runnable() {
                        @Override
                        public void run() {
                            picture = pictureReciver.getInfo();
                            mDialog.dismiss();
                            sendComment();
                        }
                    }).sendToTarget();

                } else {
                    //上传失败
                    Message.obtain(imageHandler, new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                            showToast("上传失败,请重试");
                        }
                    }).sendToTarget();
                }
            }
        }).start();
//        MultipartRequest<PictureReciver> request = new MultipartRequest<PictureReciver>(url,
//                PictureReciver.class, "123", imageFile, null, new Response.Listener<PictureReciver>() {
//            @Override
//            public void onResponse(PictureReciver response) {
//                showLog(response.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);
    }

    private void sendComment() {
        String commentStr = new_edit.getText().toString();
        if (TextUtils.isEmpty(commentStr)) {
            showToast("内容不能为空");
            return;
        }
        //转发?评论
        switch (type) {
            case COMMENT_TYPE:
                String url = null;
                switch (aty_stype) {
                    //来自Status 微博的评论[获得是Status的信息]
                    case MAIN_ATY_CODE:
                    case DETAIL_ATY_CODE:
                        url = NetParams.setComment(CommonConstants.USER_ID, status.getId(),
                                commentStr, CommonConstants.ACCESS_TOKEN.getToken());
                        break;
                    //来自评论列表的回复[获得是Comment信息]
                    case COMMENT_ADPATER_CODE:
                        commentStr = commentStr + "//" +original;
                        url = NetParams.setComment(CommonConstants.USER_ID, comment.getWid(),
                                commentStr, CommonConstants.ACCESS_TOKEN.getToken());
                        break;
                }

                GsonRequest<NormalInfo> request = new GsonRequest<NormalInfo>(Request.Method.GET,
                        url, NormalInfo.class, null,
                        new Response.Listener<NormalInfo>() {
                            @Override
                            public void onResponse(NormalInfo response) {
                                if (response.getCode() == 200) {
                                    showToast(response.getInfo());
                                    Intent data;
                                    switch (aty_stype) {
                                        //来自Status 微博的评论[获得是Status的信息]
                                        case MAIN_ATY_CODE:
                                            status.setComment(status.getComment() + 1);
                                            data = new Intent(WriteStatusActivity.this, StatusDetailActivity.class);
                                            data.putExtra(STATUS_INTENT, status);
                                            startActivity(data);
                                            break;
                                        case DETAIL_ATY_CODE:
                                            status.setComment(status.getComment() + 1);
                                            data = new Intent();
                                            data.putExtra(SEND_COMMENT_SUCCESS, true);
                                            setResult(RESULT_OK, data);
                                            break;
                                        //来自评论列表的回复[获得是Comment信息]
                                        case COMMENT_ADPATER_CODE:
                                            data = new Intent();
                                            data.putExtra(SEND_COMMENT_SUCCESS, true);
                                            setResult(RESULT_OK, data);
                                            break;
                                    }
                                    WriteStatusActivity.this.finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);

                break;
            case FORWARD_TYPE:


                /**
                 * 两种转发情况
                 * 1.原微博非转发微博
                 *    status里面不会嵌套status
                 * 2.原微博是转发微博
                 *    status里面会嵌套status
                 *    需要自己拼装content
                 */
                Status inStatus = status.getStatus();
                showLog("--->" + status.toString());
                showLog("--->" + commentStr);
                String content = commentStr;
                if (inStatus == null) {
                    url = NetParams.turnWeibo(CommonConstants.USER_ID, status.getId(), 0
                            , content, CommonConstants.ACCESS_TOKEN.getToken());

                } else {
                    content = content + "//@" + status.getUsername() + ":" + status.getContent();
                    url = NetParams.turnWeibo(CommonConstants.USER_ID, status.getId(), inStatus.getId()
                            , content, CommonConstants.ACCESS_TOKEN.getToken());
                }

                GsonRequest<NormalInfo> normalInfoGsonRequest = new GsonRequest<NormalInfo>(Request.Method.GET, url,
                        NormalInfo.class, null, new Response.Listener<NormalInfo>() {
                    @Override
                    public void onResponse(NormalInfo response) {
                        if (response.getCode() == 200) {
                            showToast(response.getInfo());
                            Intent data = new Intent();
                            data.putExtra(SEND_FORWORD_SUCCESS, true);
                            setResult(RESULT_OK, data);
//                            switch (aty_stype) {
//                                case MAIN_ATY_CODE:
//                                    data.putExtra(SEND_FORWORD_SUCCESS, true);
//                                    setResult(RESULT_OK, data);
//                                    break;
//                                case DETAIL_ATY_CODE:
//                                    data.putExtra(SEND_FORWORD_SUCCESS, true);
//                                    setResult(RESULT_OK, data);
//                                    break;
//                            }
                            WriteStatusActivity.this.finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                VolleyQueueSingleton.getInstance(this).addToRequestQueue(normalInfoGsonRequest);
                break;
            case NEW_STATUS_TYPE:
                /**
                 * 现在只能发布文字
                 */
                final String new_content = commentStr;
//                url = NetParams.sendWeibo(CommonConstants.USER_ID, commentStr, null, null, null, CommonConstants.ACCESS_TOKEN.getToken());
                GsonRequest<NormalInfo> statusRequest = new GsonRequest<NormalInfo>(Request.Method.POST, URLs.WEIBO_SEND_WEIBO, NormalInfo.class, null,
                        new Response.Listener<NormalInfo>() {
                            @Override
                            public void onResponse(NormalInfo response) {
                                Intent data = new Intent();
                                if (response.getCode() == 200) {
                                    data.putExtra(SEND_STATUS_SUCCESS, true);
                                } else {
                                    data.putExtra(SEND_FORWORD_SUCCESS, false);
                                }
                                setResult(RESULT_OK, data);
                                WriteStatusActivity.this.finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("uid", "" + CommonConstants.USER_ID);
                        map.put("content", new_content);
                        map.put("token", "" + CommonConstants.ACCESS_TOKEN.getToken());
                        if (picture != null) {
                            map.put("mini", picture.getMini());
                            map.put("medium", picture.getMedium());
                            map.put("max", picture.getMax());
                        }
                        return map;
                    }
                };
                VolleyQueueSingleton.getInstance(this).addToRequestQueue(statusRequest);
                break;
            default:
                break;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_send:
                showLog("-->" + imgUris.size());
                if (imgUris.size() > 0) {
                    sendImage(imgUris.get(0));
                } else {
                    sendComment();
                }
                break;
            case R.id.new_take_photo:
                if (imgUris.size() == 0) {
                    ImageUtils.showImagePickDialog(this);
                }
                break;
            case R.id.new_emotion:
                if (ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
                    ll_emotion_dashboard.setVisibility(View.GONE);
                    //显示输入法
                    inputMethodManager.showSoftInput(new_edit, InputMethodManager.SHOW_FORCED);
                } else {
                    ll_emotion_dashboard.setVisibility(View.VISIBLE);
                    //隐藏输入法
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(new_edit.getWindowToken(), 0);
                    }

                }
                break;
            case R.id.new_trend:
                String insertString = "##";
                int cusPostion = new_edit.getSelectionStart();
                StringBuilder sb = new StringBuilder(new_edit.getText().toString());
                sb.insert(cusPostion, insertString);
                new_edit.setText(sb.toString());
                new_edit.setSelection(cusPostion + 1);
                break;
            case R.id.new_friend:
                Intent data = new Intent(this, AtActivity.class);
                startActivityForResult(data, REQUEST_CODE_AT_USERNAME);
                break;
        }
    }

    /**
     * 更新图片显示
     */
    private void updateImgs() {
        if (imgUris.size() > 0) {
            gv_write_status.setVisibility(View.VISIBLE);
            statusImgsAdapter.notifyDataSetChanged();
        } else {
            gv_write_status.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                Uri imageUri = data.getData();
                imgUris.add(imageUri);
                updateImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                    return;
                }
                Uri imageUriFromCamera = ImageUtils.imageUriFromCamera;
                imgUris.add(imageUriFromCamera);
                updateImgs();
                break;
            case REQUEST_CODE_AT_USERNAME:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                String username = data.getStringExtra(UserAdpater.RESULT_DATA_USERNAME_AT);
                int cusPostion = new_edit.getSelectionStart();
                StringBuilder sb = new StringBuilder(new_edit.getText().toString());
                sb.insert(cusPostion, username);
                SpannableString weiboContent = StringUtils.getWeiboContent(
                        this, new_edit, sb.toString());
                new_edit.setText(weiboContent);
                new_edit.setSelection(cusPostion + username.length());
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdpater = parent.getAdapter();
        if (itemAdpater instanceof EmotionGvAdapter) {
            EmotionGvAdapter emotionAdapter = (EmotionGvAdapter) itemAdpater;

            if (position == emotionAdapter.getCount() - 1) {
                new_edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            } else {
                String emotionName = emotionAdapter.getItem(position);

                int cuPostion = new_edit.getSelectionStart();
                StringBuilder sb = new StringBuilder(new_edit.getText().toString());
                sb.insert(cuPostion, emotionName);

                SpannableString weiboContent = StringUtils.getWeiboContent(
                        this, new_edit, sb.toString());
                new_edit.setText(weiboContent);
                new_edit.setSelection(cuPostion + emotionName.length());

            }
        }
    }
}
