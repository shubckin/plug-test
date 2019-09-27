package club.mafia.plug_test;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class PlugTest extends CordovaPlugin {

    public static final String ACTION_TEST_SOMETHING = "testSomething";


    private CallbackContext context;
    private Activity activity;
    private Window window;
    private View decorView;

    //my methods:
    private CallbackContext callbackContext;
    private AndroidBug5497Workaround keyBoardSpy = null;


    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    @Override
    public void initialize(final CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);

        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Window window = cordova.getActivity().getWindow();
            }
        });
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        context = callbackContext;
        activity = cordova.getActivity();
        window = activity.getWindow();
        decorView = window.getDecorView();

        if (ACTION_TEST_SOMETHING.equals(action)) {
            this.callbackContext = callbackContext;
            return testSomething(args.getString(0));
        }

        return false;
    }

    protected void useCallback(String answer) {
        PluginResult resultA = new PluginResult(PluginResult.Status.OK, answer);
        resultA.setKeepCallback(true);
        callbackContext.sendPluginResult(resultA);
    }

    protected void useCallbackError(String answer) {
        PluginResult resultA = new PluginResult(PluginResult.Status.ERROR, answer);
        resultA.setKeepCallback(true);
        callbackContext.sendPluginResult(resultA);
    }

    protected boolean testSomething(String str) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try {
                    if (keyBoardSpy == null) {
                        keyBoardSpy = new AndroidBug5497Workaround(cordova.getActivity());
                    }
                } catch (Exception e) {
                    useCallbackError(e.toString());
                }

            }
        });

        return true;
    }


    class AndroidBug5497Workaround {

        private View mChildOfContent;
        private int usableHeightPrevious;
        private FrameLayout.LayoutParams frameLayoutParams;

        private AndroidBug5497Workaround(Activity activity) {
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            mChildOfContent = content.getChildAt(0);
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    possiblyResizeChildOfContent();
                                }
                            },
                            300);
                }
            });
            frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        }

        private void possiblyResizeChildOfContent() {
            int usableHeightNow = computeUsableHeight();
            if (usableHeightNow != usableHeightPrevious) {
                int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
                int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                if (heightDifference > (usableHeightSansKeyboard / 4)) {
                    // keyboard probably just became visible
                    //frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                    useCallback("pshow:" + usableHeightNow);
                } else {
                    // keyboard probably just became hidden
                    //frameLayoutParams.height = usableHeightSansKeyboard;
                    useCallback("phide:" + usableHeightNow);
                }
                //mChildOfContent.requestLayout();
                usableHeightPrevious = usableHeightNow;
            }
        }

        private int computeUsableHeight() {
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);
            return (r.bottom - r.top);
        }


    }

}


