package com.seid.fetawa_.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdMob {
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"; // DEV
    //private static final String AD_UNIT_ID = "ca-app-pub-9027325451795162/9933026535"; // PROD

    private boolean isLoading = false;
    private RewardedAd rewardedAd = null;
    private static Context context;

    private final MutableLiveData<Boolean> _adSuccessfulLoadedState = new MutableLiveData<>(false);
    public LiveData<Boolean> adSuccessfulLoadedState = _adSuccessfulLoadedState;

    // Private constructor to prevent instantiation from other classes
    private AdMob(Context context) {
        loadRewardedAd(context);
    }

    // Singleton instance holder
    private static class Holder {
        private static final AdMob INSTANCE = new AdMob(context);
    }

    // Public method to get the singleton instance
    public static AdMob getInstance(Context con) {
        context = con;
        return Holder.INSTANCE;
    }

    public void loadRewardedAd(Context context) {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();

            RewardedAd.load(
                    context,
                    AD_UNIT_ID,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            isLoading = false;
                            rewardedAd = null;
                            _adSuccessfulLoadedState.setValue(false);
                            Log.e("Error", adError.getMessage());
                            Log.e("Error", adError.getDomain());
                            Log.e("Error", String.valueOf(adError.getResponseInfo()));
                        }

                        @Override
                        public void onAdLoaded(RewardedAd ad) {
                            rewardedAd = ad;
                            isLoading = false;
                            _adSuccessfulLoadedState.setValue(true);
                            Log.e("Success", "--------------------------");
                        }
                    }
            );
        }
    }

    public void showRewardedVideo(Context context, Runnable onAdDismissed) {
        Activity activity = (Activity) context;

        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    rewardedAd = null;
                    _adSuccessfulLoadedState.setValue(false);
                    loadRewardedAd(context);
                    onAdDismissed.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    rewardedAd = null;
                    _adSuccessfulLoadedState.setValue(false);
                    loadRewardedAd(context);
                    onAdDismissed.run();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Ad showed fullscreen content.
                }
            });

            rewardedAd.show(activity, rewardItem -> {});
        }
    }
}
