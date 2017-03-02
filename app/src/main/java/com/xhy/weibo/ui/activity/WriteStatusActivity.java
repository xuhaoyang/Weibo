package com.xhy.weibo.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.adapter.EmotionGvAdapter;
import com.xhy.weibo.adapter.EmotionPagerAdapter;
import com.xhy.weibo.adapter.WriteStatusGridImgsAdapter;
import com.xhy.weibo.ui.base.BaseActivity;
import com.xhy.weibo.model.Comment;
import com.xhy.weibo.entity.Picture;
import com.xhy.weibo.entity.PictureReciver;
import com.xhy.weibo.logic.CommentLogic;
import com.xhy.weibo.logic.StatusLogic;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Status;
import com.xhy.weibo.network.ImageUpload;
import com.xhy.weibo.network.NetParams;
import com.xhy.weibo.utils.Constants;
import com.xhy.weibo.utils.EmotionUtils;
import com.xhy.weibo.utils.ImageUtils;
import com.xhy.weibo.utils.StringUtils;
import com.xhy.weibo.ui.widget.WrapHeightGridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hk.xhy.android.commom.utils.ActivityUtils;
import hk.xhy.android.commom.utils.ConvertUtils;
import hk.xhy.android.commom.utils.ScreenUtils;


public class WriteStatusActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {


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

    @BindView(R.id.main_Car)
    CoordinatorLayout mCoordinatorLayout;
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
                ActivityUtils.finishActivity();
            }
        });

        //默认转发评论都无法发图片
        type = getIntent().getIntExtra(Constants.TYPE, 101);
        aty_stype = getIntent().getIntExtra(Constants.TAG, 1);
        switch (type) {
            case Constants.COMMENT_TYPE:
                //获取待评论微博数据
                switch (aty_stype) {
                    //来自Status 微博的评论[获得是Status的信息]
                    case Constants.MAIN_ATY_CODE:
                    case Constants.DETAIL_ATY_CODE:
                        /**
                         * 因为这里传输方式 会变成json形式 保留原有的获取数据的方式
                         */
                        Object val = getIntent().getSerializableExtra(Constants.STATUS_INTENT);

                        if (val instanceof String) {//前者如果不是序列化来的数据,会未空
                            status = Status.parseObject(getIntent().getStringExtra(Constants.STATUS_INTENT));
                        } else if (val instanceof Status) {
                            status = (Status) val;
                        } else {
                            ActivityUtils.finishActivity();
                        }
                        getSupportActionBar().setTitle("发表评论");
                        new_take_photo.setVisibility(View.GONE);
                        break;
                    //来自评论列表的回复[获得是Comment信息]
                    case Constants.COMMENT_ADPATER_CODE:
                        Object valc = getIntent().getSerializableExtra(Constants.COMMENT_INTENT);
                        if (valc instanceof String) {//前者如果不是序列化来的数据,会未空
                            comment = Comment.parseObject(getIntent().getStringExtra(Constants.COMMENT_INTENT));
                        } else if (valc instanceof Comment) {
                            comment = (Comment) valc;
                        } else {
                            ActivityUtils.finishActivity();
                        }

                        getSupportActionBar().setTitle("回复评论");
                        original = "@" + comment.getUsername() + ": " + comment.getContent();
                        new_edit.setHint(original);
                        new_take_photo.setVisibility(View.GONE);
                        break;
                }
                break;
            case Constants.FORWARD_TYPE:
                new_take_photo.setVisibility(View.GONE);
                Object val = getIntent().getSerializableExtra(Constants.STATUS_INTENT);

                if (val instanceof String) {//前者如果不是序列化来的数据,会未空
                    status = Status.parseObject(getIntent().getStringExtra(Constants.STATUS_INTENT));
                } else if (val instanceof Status) {
                    status = (Status) val;
                } else {
                    ActivityUtils.finishActivity();
                }

//                status = (Status) getIntent().getSerializableExtra(Constants.STATUS_INTENT);
                getSupportActionBar().setTitle("转发微博");
                break;
            case Constants.NEW_STATUS_TYPE:
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
        int screenWidth = ScreenUtils.getScreenWidth(this);
        int spacing = ConvertUtils.dp2px(this, 8);

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
        final String url = NetParams.uploadUserPic(AppConfig.getAccessToken().getToken(), height, width);

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
                            showSnackbar("上传失败,请重试");
                        }
                    }).sendToTarget();
                }
            }
        }).start();

    }

    private void sendComment() {
        String commentStr = new_edit.getText().toString();
        if (TextUtils.isEmpty(commentStr)) {
            showToast("内容不能为空");
            return;
        }

        //禁止重复点击
        new_send.setClickable(false);

        //转发?评论
        switch (type) {
            case Constants.COMMENT_TYPE:
                String url = null;
                //父级微博id
                int pWid = 0;
                CommentLogic.SetCommentCallBack callBack = new CommentLogic.SetCommentCallBack() {
                    @Override
                    public void onSetCommentSuccess(Result result) {
                        showToast(result.getMsg());
                        Intent data;
                        switch (aty_stype) {
                            //来自Status 微博的评论[获得是Status的信息]
                            case Constants.MAIN_ATY_CODE:
                                status.setComment(status.getComment() + 1);
                                data = new Intent(WriteStatusActivity.this, StatusDetailActivity.class);
                                data.putExtra(Constants.STATUS_INTENT, status);
                                startActivity(data);
                                break;
                            case Constants.DETAIL_ATY_CODE:
                                status.setComment(status.getComment() + 1);
                                data = new Intent();
                                data.putExtra(Constants.SEND_COMMENT_SUCCESS, true);
                                setResult(RESULT_OK, data);
                                break;
                            //来自评论列表的回复[获得是Comment信息]
                            case Constants.COMMENT_ADPATER_CODE:
                                data = new Intent();
                                data.putExtra(Constants.SEND_COMMENT_SUCCESS, true);
                                setResult(RESULT_OK, data);
                                break;
                        }
                        ActivityUtils.finishActivity();
                    }

                    @Override
                    public void onSetCommentFailure(String message) {
                        showToast(message);
                    }

                    @Override
                    public void onSetCommentError(Throwable t) {
                        showLog(t.getMessage());
                    }
                };

                switch (aty_stype) {
                    //来自Status 微博的评论[获得是Status的信息]
                    case Constants.MAIN_ATY_CODE:
                    case Constants.DETAIL_ATY_CODE:


                        if (status.getStatus() != null) {
                            pWid = status.getStatus().getId();
                        } else {
                            pWid = status.getId();
                        }
                        CommentLogic.setComment(AppConfig.getUserId(), status.getId(), pWid,
                                commentStr, AppConfig.getAccessToken().getToken(), callBack);

                        break;
                    //来自评论列表的回复[获得是Comment信息]
                    case Constants.COMMENT_ADPATER_CODE:
                        commentStr = commentStr + "//" + original;

                        pWid = comment.getWid();
                        CommentLogic.setComment(AppConfig.getUserId(), comment.getWid(), pWid,
                                commentStr, AppConfig.getAccessToken().getToken(), callBack);

                        break;
                }
                break;
            case Constants.FORWARD_TYPE:

                /**
                 * 两种转发情况
                 * 1.原微博非转发微博
                 *    status里面不会嵌套status
                 * 2.原微博是转发微博
                 *    status里面会嵌套status
                 *    需要自己拼装content
                 */
                Status inStatus = status.getStatus();
                int inStatusId = 0;
                String content = commentStr;
                if (inStatus != null) {
                    content = content + "//@" + status.getUsername() + ":" + status.getContent();
                    inStatusId = inStatus.getId();
                }

                StatusLogic.turnWeibo(AppConfig.getUserId(), status.getId(), inStatusId,
                        content, AppConfig.getAccessToken().getToken(), new StatusLogic.TurnWeiboCallBack() {
                            @Override
                            public void onTurnSuccess(Result result) {
                                showSnackbar(result.getMsg());
                                Intent data = new Intent();
                                data.putExtra(Constants.SEND_FORWORD_SUCCESS, true);
                                setResult(RESULT_OK, data);
                                ActivityUtils.finishActivity();
                            }

                            @Override
                            public void onTurnFailure(String message) {
                                showSnackbar(message);
                                showLog(message);
                            }

                            @Override
                            public void onTurnError(Throwable t) {
                                showLog(t.getMessage());
                            }
                        });
                break;
            case Constants.NEW_STATUS_TYPE:

                final String new_content = commentStr;
                StatusLogic.SendWeiboCallBack sendWeiboCallBack = new StatusLogic.SendWeiboCallBack() {
                    @Override
                    public void onSendSuccess(Result result) {
                        Intent data = new Intent();
                        data.putExtra(Constants.SEND_STATUS_SUCCESS, true);
                        setResult(RESULT_OK, data);
                        ActivityUtils.finishActivity();
                    }

                    @Override
                    public void onSendFailure(String message) {
                        showSnackbar(message);
                        showLog(message);
                    }

                    @Override
                    public void onSendError(Throwable t) {
                        showLog(t.getMessage());
                    }
                };

                if (picture != null) {
                    StatusLogic.sendWeibo(AppConfig.getUserId(), new_content, AppConfig.getAccessToken().getToken()
                            , picture.getMini(), picture.getMedium(), picture.getMax(), sendWeiboCallBack);

                } else {
                    StatusLogic.sendWeibo(AppConfig.getUserId(), new_content, AppConfig.getAccessToken().getToken()
                            , null, null, null, sendWeiboCallBack);
                }
                break;
            default:
                break;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_send:
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
                String username = data.getStringExtra(Constants.RESULT_DATA_USERNAME_AT);
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

    private void showSnackbar(String msg) {
        showSnackbar(msg, Snackbar.LENGTH_SHORT);
    }

    private void showSnackbar(String msg, int length) {
        Snackbar.make(mCoordinatorLayout, msg, length).show();
    }
}
