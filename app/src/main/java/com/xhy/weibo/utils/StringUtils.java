package com.xhy.weibo.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.xhy.weibo.R;
import com.xhy.weibo.activity.UserInfoActivity;
import com.xhy.weibo.activity.SearchActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuhaoyang on 16/5/14.
 */
public class StringUtils {

    public static SpannableString getWeiboContent(final Context context, final TextView tv, String source) {
        String regexAt = "@[\u4e00-\u9fa5\\w]+";
        String regexTopic = "#[\u4e00-\u9fa5\\w]+#";
        String regexEmoji = "\\[[\u4e00-\u9fa5\\w]+\\]";
        String regex = "(" + regexAt + ")|(" + regexTopic + ")|(" + regexEmoji + ")";


        SpannableString spannableString = new SpannableString(source);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(spannableString);

        if (matcher.find()){
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            matcher.reset();
        }


        while (matcher.find()) {
            final String atiStr = matcher.group(1);
            final String topicStr = matcher.group(2);
            final String emojiStr = matcher.group(3);

            if (atiStr != null) {
                int start = matcher.start(1);
                WeiboClickableSpan clickableSpan = new WeiboClickableSpan(context) {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(context, UserInfoActivity.class);
                        intent.putExtra(UserInfoActivity.USER_NAME, atiStr.substring(1));
                        context.startActivity(intent);
                    }
                };

                spannableString.setSpan(clickableSpan, start, start + atiStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            if (topicStr != null) {
                int start = matcher.start(2);
                WeiboClickableSpan clickableSpan = new WeiboClickableSpan(context) {
                    @Override
                    public void onClick(View widget) {
                        Intent data = new Intent(context, SearchActivity.class);
                        data.putExtra(SearchActivity.SEARCH_CONTENT, topicStr.substring(1,topicStr.length()));
                        context.startActivity(data);

//                        ToastUtils.showToast(context, "话题" + topicStr, Toast.LENGTH_SHORT);
                    }
                };

                spannableString.setSpan(clickableSpan, start, start + topicStr.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

            if (emojiStr != null) {
                int start = matcher.start(3);
                int imgRes = EmotionUtils.getImgByName(emojiStr);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgRes);

                //可能没有这个图片 要非空判断
                if (bitmap != null) {
                    int size = (int) tv.getTextSize();
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

                    ImageSpan imageSpan = new ImageSpan(context, bitmap);
                    spannableString.setSpan(imageSpan, start, start + emojiStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }


        }

        return spannableString;
    }

    static class WeiboClickableSpan extends ClickableSpan {

        private Context context;

        public WeiboClickableSpan(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View widget) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(context, R.color.txt_at_blue));
            ds.setUnderlineText(false);//无下划线
        }
    }
}
