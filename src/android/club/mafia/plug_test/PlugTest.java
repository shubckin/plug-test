package club.mafia.plug_test;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlugTest extends CordovaPlugin {

    public static final String ACTION_KEYBOARD_CALLBACK = "setKeyboardCallback";


    private CallbackContext context;
    private Activity activity;
    private Window window;
    private View decorView;

    //my methods:
    private CallbackContext keyboardCallbackContext;
    private KeyboardHandler keyBoardHandler = null;

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

        if (ACTION_KEYBOARD_CALLBACK.equals(action)) {
            return setKeyboardCallback(callbackContext);
        }

        return false;
    }

    protected void useCallback(CallbackContext callbackContext, JSONObject answer) {
        PluginResult resultA = new PluginResult(PluginResult.Status.OK, answer);
        resultA.setKeepCallback(true);
        callbackContext.sendPluginResult(resultA);
    }

    protected void useCallbackError(CallbackContext callbackContext, String answer) {
        PluginResult resultA = new PluginResult(PluginResult.Status.ERROR, answer);
        resultA.setKeepCallback(true);
        callbackContext.sendPluginResult(resultA);
    }

    protected boolean setKeyboardCallback(CallbackContext callbackContext) {
        this.keyboardCallbackContext = callbackContext;

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    if (keyBoardHandler == null) {
                        keyBoardHandler = new KeyboardHandler(cordova.getActivity());
                    }
                } catch (Exception e) {
                    useCallbackError(keyboardCallbackContext, e.toString());
                }
            }
        });
        return true;
    }

    class KeyboardHandler {

        private View mChildOfContent;
        private int usableHeightPrevious;
        private FrameLayout.LayoutParams frameLayoutParams;

        private KeyboardHandler(Activity activity) {
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            mChildOfContent = content.getChildAt(0);
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    inspectDelayed(300);
                    inspectDelayed(600);
                    inspectDelayed(900);
                }
            });
            frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        }

        private void inspectDelayed(int delay) {
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        possiblyResizeChildOfContent();
                    } catch (Exception e) {
                        useCallbackError(keyboardCallbackContext, e.toString());
                    }
                }
            }, delay);
        }

        private void possiblyResizeChildOfContent() throws Exception {
            int usableHeightNow = computeUsableHeight();
            if (usableHeightNow != usableHeightPrevious) {
                int totalHeight = mChildOfContent.getRootView().getHeight();
                int heightDifference = totalHeight - usableHeightNow;

                JSONObject result = new JSONObject();
                result.put("heightTotal", totalHeight);
                result.put("heightLeft", usableHeightNow);
                result.put("heightKeyboard", heightDifference);


                if (heightDifference > (totalHeight / 4)) {
                    // keyboard probably just became visible
                    //frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                    result.put("isVisible", true);
                } else {
                    // keyboard probably just became hidden
                    //frameLayoutParams.height = usableHeightSansKeyboard;
                    result.put("isVisible", false);
                }
                //mChildOfContent.requestLayout();
                usableHeightPrevious = usableHeightNow;
                useCallback(keyboardCallbackContext, result);
            }
        }

        private int computeUsableHeight() {
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);
            return (r.bottom - r.top);
        }
    }
}