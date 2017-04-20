package com.vmax.android.ads.mediation.partners;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;
import com.vmax.android.ads.api.VmaxAdPartner;
import com.vmax.android.ads.api.VmaxAdSettings;
import com.vmax.android.ads.mediation.partners.VmaxCustomAd;
import com.vmax.android.ads.mediation.partners.VmaxCustomAdListener;
import com.vmax.android.ads.util.Constants;

import java.util.HashMap;
import java.util.Map;


/*
 * Tested with Inmobi SDK 6.0.4.
 */
public class InmobiBanner extends VmaxCustomAd implements InMobiBanner.BannerAdListener {

    private static final String ACCOUNT_ID = "accountid";
    private static final String PLACEMENT_ID = "placementid";
    private VmaxCustomAdListener mBannerListener;
    private InMobiBanner banner;
    private ViewGroup adLayout;
    private boolean isCacheAd = false;
    public boolean LOGS_ENABLED = true;
    private int width, height;
    private Context context;
    private VmaxAdSettings vmaxAdSettings;
    private VmaxAdPartner vmaxAdPartner;
    @Override
    public void loadAd(final Context context,
                       final VmaxCustomAdListener vmaxCustomAdListener,
                       final Map<String, Object> localExtras,
                       final Map<String, Object> serverExtras) {

        if (LOGS_ENABLED) {
            Log.i("vmax", "Inmobi Banner");
            Log.d("vmax", "Inmobi version:  " + InMobiSdk.getVersion());
        }

        try {
            this.context = context;
            mBannerListener = vmaxCustomAdListener;
            final String placementid, accountid;
            if (localExtras != null) {

                if (localExtras.containsKey("vmaxAdPartner"))
                {
                    vmaxAdPartner = (VmaxAdPartner)localExtras.get("vmaxAdPartner");
                    Log.d("vmax","VmaxAdPartnerName "+ "Inmobi");
                    vmaxAdPartner.setPartnerName("Inmobi");
                    Log.d("vmax","VmaxAdPartnerSDKVersion "+ InMobiSdk.getVersion());
                    vmaxAdPartner.setPartnerSDKVersion(InMobiSdk.getVersion());
                }

                if (localExtras.containsKey("vmaxAdSettings")) {
                    vmaxAdSettings=(VmaxAdSettings) localExtras.get("vmaxAdSettings");
                }

                if (localExtras.containsKey("location")) {
                    if (LOGS_ENABLED) {
                        Log.i("vmax",
                                "location : "
                                        + (Location) localExtras
                                        .get("location"));
                    }

                    InMobiSdk.setLocation((Location) localExtras
                            .get("location"));


                }
                if (localExtras.containsKey("gender")) {
                    if (LOGS_ENABLED) {
                        Log.i("vmax", "Gender : "
                                + localExtras.get("gender").toString());
                    }
                    if (localExtras.get("gender").toString()
                            .equalsIgnoreCase("male")) {
                        InMobiSdk.setGender(InMobiSdk.Gender.MALE);

                    } else if (localExtras.get("gender").toString()
                            .equalsIgnoreCase("female")) {
                        InMobiSdk.setGender(InMobiSdk.Gender.FEMALE);

                    } else if (localExtras.get("gender").toString()
                            .equalsIgnoreCase("unknown")) {
                        InMobiSdk.setGender(InMobiSdk.Gender.valueOf(localExtras.get("gender").toString()));

                    }
                }

                if (localExtras.containsKey("age")) {
                    if (LOGS_ENABLED) {
                        Log.i("vmax", "age : "
                                + localExtras.get("age").toString());
                    }
                    InMobiSdk.setAge(Integer.parseInt(localExtras.get("age").toString()));
                }

                if (localExtras.containsKey("language")) {
                    if (LOGS_ENABLED) {
                        Log.i("vmax", "language : "
                                + localExtras.get("language").toString());
                    }
                    InMobiSdk.setLanguage(localExtras.get("language").toString());
                }

                if (localExtras.containsKey("keyword")) {

                    if (LOGS_ENABLED) {
                        Log.i("vmax",
                                "keyword : "
                                        + (String) localExtras
                                        .get("keyword"));
                    }
                    InMobiSdk.setInterests((String) localExtras
                            .get("keyword"));

                }
                if (localExtras.containsKey("adview")) {
                    adLayout = (ViewGroup) localExtras.get("adview");
                }
                if (localExtras.containsKey("cacheAd")) {
                    isCacheAd = (Boolean) localExtras.get("cacheAd");
                }

                String isInlineDisplay="";
                if(localExtras.containsKey("isInlineDisplay")){
                    isInlineDisplay=localExtras.get("isInlineDisplay").toString();
                }

                if (vmaxAdSettings!=null && !TextUtils.isEmpty(vmaxAdSettings.getAdSize())) {
                    String tempdimension = vmaxAdSettings.getAdSize();
                    try {
                        String[] separated = tempdimension.split("x");
                        width = Integer.parseInt(separated[0]);
                        height = Integer.parseInt(separated[1]);

                        Log.i("vmax", "width: " + width + " ,height: " + height);
                    } catch (NumberFormatException ne) {
                        if(mBannerListener!=null)
                            mBannerListener.onAdFailed(Constants.AdError.ERROR_INVALID_REQUEST_ARGUMENTS,"InmobiBanner "+ne.getMessage());
                        Log.i("vmax", "Non integer size is passed for width/height");
                        return;
                    } catch (Exception ne) {
                        if(mBannerListener!=null)
                            mBannerListener.onAdFailed(Constants.AdError.ERROR_INVALID_REQUEST_ARGUMENTS,"InmobiBanner "+ne.getMessage());
                        ne.printStackTrace();
                        return;
                    }

                } else {
                    if(isInlineDisplay.equalsIgnoreCase("true")){
                        width = 320;
                        height = 250;
                    }
                    else {
                        width = 320;
                        height = 50;
                    }
                }
            } else {
                width = 320;
                height = 50;
            }
            if (extrasAreValid(serverExtras)) {
                placementid = serverExtras.get(PLACEMENT_ID).toString();
                accountid = serverExtras.get(ACCOUNT_ID).toString();

                if (LOGS_ENABLED) {
                    Log.d("vmax", "Inside Inmobi placementid " + placementid);
                    Log.d("vmax", "Inside Inmobi accountid " + accountid);
                }
                if(!Constants.isInmobiSDKInitialised)
                {
                    InMobiSdk.init((Activity) context, accountid);
                    Constants.isInmobiSDKInitialised = true;
                    Log.i("vmax","Inmobi initialised");
                }

                try {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("tp", "c_vmax");
                    Log.d("vmax", "Inside InmobiNative tp " + map.get("tp"));
                    map.put("tp-ver", Constants.VersionDetails.LIBRARY_VERSION); // Mediation partners provide Mediator's SDK Version.
                    Log.d("vmax", "Inside InmobiNative tp-ver " + map.get("tp-ver"));
                    banner = new InMobiBanner(((Activity) context), Long.parseLong(placementid));
                    banner.removeAllViews();
                    adLayout.removeAllViews();
                    adLayout.setVisibility(View.VISIBLE);
                    /*RelativeLayout relativeLayout = new RelativeLayout(context);
                    relativeLayout.addView(banner, new RelativeLayout.LayoutParams(320,50));*/
                    int w = toPixelUnits(width);
                    int h = toPixelUnits(height);
                    adLayout.addView(banner, new ViewGroup.LayoutParams(w, h));
                    banner.setEnableAutoRefresh(false);
                    banner.setListener(this);
                    banner.setExtras(map);
                    banner.load();
                } catch (Exception e) {
                    Log.i("vmax", "Placement id is not properly configured");
                    if(mBannerListener!=null)
                        mBannerListener.onAdFailed(Constants.AdError.ERROR_INVALID_REQUEST_ARGUMENTS,"InmobiBanner Placement id is not properly configured");
                    return;
                }

            } else {
                if (mBannerListener != null) {
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_MANDATORY_PARAM_MISSING,"InmobiBanner Mandatory parameters missing");
                }
                return;
            }

        } catch (Exception e) {
            if (mBannerListener != null) {
                mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN,"InmobiBanner "+e.getMessage());
            }
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void showAd() {
        try {
            if (adLayout != null) {
                adLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onInvalidate() {
        Log.i("vmax", "Inmobi Banner onInvalidate");

        if (banner != null) {
            banner.setListener(null);
            banner = null;
        }
    }

    @Override
    public void onAdLoadSucceeded(InMobiBanner inMobiBanner) {
        Log.i("vmax", "Inmobi Banner onAdLoadSucceeded");
        adLayout.setVisibility(View.GONE);
        if (!isCacheAd) {
            showAd();
        }
        mBannerListener.onAdLoaded(null);
    }

    @Override
    public void onAdLoadFailed(InMobiBanner inMobiBanner, InMobiAdRequestStatus inMobiAdRequestStatus) {
        if (inMobiAdRequestStatus != null) {
            Log.i("vmax", "Inmobi Banner onAdLoadFailed : " + inMobiAdRequestStatus.getMessage());
            if (mBannerListener != null) {
                if(inMobiAdRequestStatus.getStatusCode().ordinal()==0)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==1)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_NETWORK_ERROR,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==2)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_NOFILL,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==3)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_INVALID_REQUEST_ARGUMENTS,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==4)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_ADREQUEST_NOT_ALLOWED,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==5)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_TIMEOUT,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==6)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==7)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_INTERNAL_SERVER,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==8)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_ADREQUEST_NOT_ALLOWED,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==9)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_ADREQUEST_NOT_ALLOWED,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else if(inMobiAdRequestStatus.getStatusCode().ordinal()==10)
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_AD_EXPIRED,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
                else
                    mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN,"InmobiBanner "+inMobiAdRequestStatus.getStatusCode().toString());
            }
        } else {
            Log.i("vmax", "Inmobi Banner onAdLoadFailed : " + "No Ad in inventory");
            if (mBannerListener != null) {
                mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN,"InmobiBanner Unknown error");
            }
        }
    }

    @Override
    public void onAdDisplayed(InMobiBanner inMobiBanner) {
        Log.i("vmax", "Inmobi Banner AdDisplayed  : ");
        if (mBannerListener != null) {
            mBannerListener.onAdShown();
        }
    }

    @Override
    public void onAdDismissed(InMobiBanner inMobiBanner) {
        Log.i("vmax", "Inmobi Banner onAdDismissed ");
        if (mBannerListener != null) {
            mBannerListener.onAdDismissed();
        }
    }

    @Override
    public void onAdInteraction(InMobiBanner inMobiBanner, Map<Object, Object> map) {
        Log.i("vmax", "Inmobi Banner onAdInteraction ");
        if (mBannerListener != null) {
            mBannerListener.onAdClicked();
        }
    }

    @Override
    public void onUserLeftApplication(InMobiBanner inMobiBanner) {
        Log.i("vmax", "Inmobi Banner onUserLeftApplication ");
        if (mBannerListener != null) {
            mBannerListener.onLeaveApplication();
        }
    }

    @Override
    public void onAdRewardActionCompleted(InMobiBanner inMobiBanner, Map<Object, Object> map) {

    }

    private boolean extrasAreValid(Map<String, Object> serverExtras) {
        return serverExtras.containsKey(ACCOUNT_ID)
                && serverExtras.containsKey(PLACEMENT_ID);
    }


    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroy() {

    }

    private int toPixelUnits(int dipUnit) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dipUnit * density);
    }
}
