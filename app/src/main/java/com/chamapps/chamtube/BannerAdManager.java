package com.chamapps.chamtube;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

/**
 * مدير بسيط للشاشات البانر باستخدام Google Mobile Ads (AdMob).
 * - استبدل adUnitId بالقيمة الخاصة بك عند الاستدعاء.
 * - يتوقع أن يتم تمرير Container (FrameLayout) في واجهة المستخدم حيث يوضع البانر.
 */
public class BannerAdManager {
    private final Activity activity;
    private AdView currentBanner;
    private final FrameLayout adContainer;

    public BannerAdManager(Activity activity, FrameLayout adContainer) {
        this.activity = activity;
        this.adContainer = adContainer;
        MobileAds.initialize(activity);
    }

    /**
     * تحميل بانر بعرض افتراضي (مثلاً 320dp) أو العرض المناسب للشاشة.
     * @param adUnitId وحدة الإعلان (مثال: ca-app-pub-xxxxx/xxxxx)
     * @param widthDp العرض بالـ dp الذي تريد أن يكون الأساس لحساب حجم البانر التكيفي
     */
    public void loadBanner(final String adUnitId, final int widthDp) {
        activity.runOnUiThread(() -> {
            // نظف بانر سابق إن وُجد
            if (currentBanner != null) {
                adContainer.removeView(currentBanner);
                currentBanner.destroy();
                currentBanner = null;
            }

            currentBanner = new AdView(activity);
            currentBanner.setAdUnitId(adUnitId);

            // استخدم AdSize التكيفي الموصى به
            AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp);
            currentBanner.setAdSize(adSize);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            adContainer.addView(currentBanner, params);

            AdRequest adRequest = new AdRequest.Builder().build();

            currentBanner.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // إعلان جاهز
                }

                @Override
                public void onAdFailedToLoad(LoadAdError adError) {
                    // فشل في تحميل الإعلان - سجّل أو تعامل مع الحالة
                    // adError.getMessage() يعطي تفاصيل
                }

                @Override
                public void onAdOpened() { }

                @Override
                public void onAdClosed() { }
            });

            currentBanner.loadAd(adRequest);
        });
    }

    public void destroy() {
        activity.runOnUiThread(() -> {
            if (currentBanner != null) {
                currentBanner.destroy();
                adContainer.removeView(currentBanner);
                currentBanner = null;
            }
        });
    }
}
