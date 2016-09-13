package com.paymentez.paymentezexample.todo1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paymentez.paymentezexample.R;
import com.rsa.mobilesdk.sdk.MobileAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Test class for demonstration of the sdk usage. It presents the list of
 * available API methods. When clicking a list item, there is displayed dialog
 * box with the requested information. The non-implemented API entries are
 * disabled for the while. The disabled items have gray background color.
 *
 * @author serg
 *
 */
public class Todo1CollectActivity extends AppCompatActivity {

    private static final String TAG = Todo1CollectActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 153;//A unique code in this app
    /**
     * Properties values
     */

    /**
     * the configuration property value
     */
    public static final int CONFIGURATION_VALUE = MobileAPI.COLLECT_ALL_DEVICE_DATA_AND_LOCATION;
    /**
     * max period of the waiting for location value (ms)
     */
    public static final int TIMEOUT_VALUE = MobileAPI.TIMEOUT_DEFAULT_VALUE;
    /**
     * the max age of the best location, minutes value
     */
    public static final int BEST_LOCATION_AGE_MINUTES_VALUE = MobileAPI.BEST_LOCATION_AGE_MINUTES_DEFAULT_VALUE;
    /**
     * the max age of the oldest suitable location, days value
     */
    public static final int MAX_LOCATION_AGE_DAYS_VALUE = MobileAPI.MAX_LOCATION_AGE_DAYS_DEFAULT_VALUE;
    /**
     * the max suitable horizontal accuracy, meters value
     */
    public static final int MAX_ACCURACY_VALUE = MobileAPI.MAX_ACCURACY_DEFAULT_VALUE;

    /**
     * special command codes
     */
    private static final int COMMAND_RESTART_COLLECTION = 0;
    private static final int COMMAND_SET_COLLECTION_MODE = 1;
    private static final int COMMAND_DISPLAY_JSON = 2;
    private static final int COMMAND_SAVE_JSON = 3;
    private static final int COMMAND_ADD_CUSTOM_DATA = 4;

    static final int IMMEDIATE_RESULT_DIALOG = 1;
    static final int PROGRESS_DIALOG = 2;
    static final int ERROR_DIALOG = 3;
    static final int STRING_INPUT_DIALOG = 4;

    static final String DIALOG_ID_KEY = "dialogIdKey";
    static final String DIALOG_TEXT_KEY = "messageKey";
    static final String DIALOG_TITLE_KEY = "messageTitleKey";

    private static final String CONFIGURATION_KEY = "configurationKey";
    private static final String TITLE_KEY = "title";
    private static final String RESULT_KEY = "result";

    /** current collection mode */
    private int mConfiguration = CONFIGURATION_VALUE;

    private ListFragment lf;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lf = new SdkListFragment();

        Bundle b = new Bundle();
        b.putInt(CONFIGURATION_KEY, mConfiguration);
        lf.setArguments(b);


        requestPermissions(CONFIGURATION_VALUE);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, lf).commit();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        return super.onMenuOpened(featureId, menu);
    }

    public static class SdkListFragment extends ListFragment {
        private String[] mSpecialCommands;
        private String[] mInfoItems;
        private TestListAdapter mAdapter;
        private String mResultStr;
        private String mTitleStr;
        private int configuration;
        private ItemDialogFragment idf;
        private JSONObject mJDeviceInfo;
        private String[] mJSONKey;
        private String[] mJSONLocationKey;
        private String[] mJSONNetworksDataKey;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            idf = new ItemDialogFragment();

            Bundle arguments = getArguments();

            configuration = arguments.getInt(CONFIGURATION_KEY);

            Resources res = getResources();

            mSpecialCommands = res.getStringArray(R.array.special_commands);
            mInfoItems = res.getStringArray(R.array.info_items);
            mJSONKey = res.getStringArray(R.array.json_key);
            mJSONLocationKey = res.getStringArray(R.array.json_location_key);
            mJSONNetworksDataKey = res.getStringArray(R.array.json_networks_data_key);

            if (savedInstanceState != null) {
                mTitleStr = savedInstanceState.getString(TITLE_KEY);
                mResultStr = savedInstanceState.getString(RESULT_KEY);
            }

            // Build common special commands and items list
            int i;
            int commonLength = mSpecialCommands.length + mInfoItems.length;
            String[] commonList = new String[commonLength];
            for (i = 0; i < mSpecialCommands.length; i++) {
                commonList[i] = mSpecialCommands[i];
            }
            for (i = mSpecialCommands.length; i < commonLength; i++) {
                commonList[i] = mInfoItems[i - mSpecialCommands.length];
            }

            mAdapter = new TestListAdapter(getActivity(), R.layout.row, commonList);
            setListAdapter(mAdapter);
            getListView().setBackgroundColor(Color.WHITE);
            getListView().setCacheColorHint(0);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            if (position < mSpecialCommands.length) {
                // this is a command
                switch (position) {
                    case COMMAND_RESTART_COLLECTION:
                        doRestartCollection();
                        break;
                    case COMMAND_SET_COLLECTION_MODE:
                        doSetCollectionMode();
                        break;
                    case COMMAND_DISPLAY_JSON:
                        doDisplayJSON();
                        break;
                    case COMMAND_SAVE_JSON:
                        doSaveJSON();
                        break;
                    case COMMAND_ADD_CUSTOM_DATA:
                        showFragmentDialog(STRING_INPUT_DIALOG, mTitleStr, mResultStr);
                        break;
                }
            } else {
                // item
                int index = position - mSpecialCommands.length;
                mResultStr = getDeviceInfo(index);
                if (mResultStr == null || mResultStr.equals("")) {
                    final Resources res = getResources();
                    mResultStr = res.getString(R.string.not_available);
                }
                mTitleStr = mInfoItems[index];
                showFragmentDialog(IMMEDIATE_RESULT_DIALOG, mTitleStr, mResultStr);
            }
        }

        /**
         * Adapter
         */
        private class TestListAdapter extends ArrayAdapter<String> {
            public TestListAdapter(Context context, int textViewResourceId, String[] list) {
                super(context, textViewResourceId, list);
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;

                if (row == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    row = inflater.inflate(R.layout.row, parent, false);
                }

                TextView tv = (TextView) row.findViewById(R.id.test_list_item);
                String text = "";
                int textColor = 0;
                if (position < mSpecialCommands.length) {
                    text = mSpecialCommands[position];
                    textColor = getResources().getColor(R.color.command_color);
                    if (position == COMMAND_SET_COLLECTION_MODE) {
                        text += configuration;
                    }
                } else {
                    int index = position - mSpecialCommands.length;
                    text = mInfoItems[index];
                    if (isEnabled(index)) {
                        textColor = getResources().getColor(R.color.item_color);
                    } else {
                        textColor = getResources().getColor(R.color.item_disabled_color);
                    }
                }
                tv.setText(text);
                tv.setTextColor(textColor);
                return row;
            }
        }

        /**
         * restarts collection by destroying the SDK and re-initializing
         */
        private void doRestartCollection() {
            MobileAPI mobileAPI = MobileAPI.getInstance(getActivity());
            mobileAPI.destroy();
            //requestPermissions();
            mobileAPI.initSDK(getSdkProperties());
            Toast.makeText(getActivity(), "Data collection restarted", Toast.LENGTH_LONG).show();
        }

        /**
         * change the collection mode, and restart collection
         */
        private void doSetCollectionMode() {
            switch (configuration) {
                case MobileAPI.COLLECT_BASIC_DEVICE_DATA_ONLY:
                    configuration = MobileAPI.COLLECT_DEVICE_DATA_ONLY;
                    break;
                case MobileAPI.COLLECT_DEVICE_DATA_ONLY:
                    configuration = MobileAPI.COLLECT_BASIC_DEVICE_DATA_ONLY;
                    break;
                case MobileAPI.COLLECT_ALL_DEVICE_DATA_AND_LOCATION:
                    configuration = MobileAPI.COLLECT_ALL_DEVICE_DATA_AND_LOCATION;
                    break;
            }
            mAdapter.notifyDataSetChanged();
            doRestartCollection();
        }

        /**
         * show the JSON string with collected info
         */
        private void doDisplayJSON() {
            mTitleStr = "JSON String";
            MobileAPI mobileAPI = MobileAPI.getInstance(getActivity());
            mResultStr = mobileAPI.collectInfo();

            showFragmentDialog(IMMEDIATE_RESULT_DIALOG, mTitleStr, mResultStr);
        }

        /**
         * save the JSON string to a file on the SD card
         */
        private void doSaveJSON() {
            MobileAPI mobileAPI = MobileAPI.getInstance(getActivity());
            String jsonStr = mobileAPI.collectInfo();

            File sdcardRoot = Environment.getExternalStorageDirectory();
            if (sdcardRoot == null) {
                showError("can't get sd card root");
                return;
            }
            File jsonRoot = new File(sdcardRoot, "rsa_json");
            if (!jsonRoot.exists()) {
                jsonRoot.mkdirs();
                if (!jsonRoot.exists()) {
                    showError("can't create directory: " + jsonRoot.getAbsolutePath());
                    return;
                }
            } else {
                if (!jsonRoot.isDirectory()) {
                    showError("not a directory, please remove file: " + jsonRoot.getAbsolutePath());
                    return;
                }
            }

            String fname = "json_" + System.currentTimeMillis() + ".txt";
            File jsonFile = new File(jsonRoot, fname);
            FileOutputStream fout = null;
            OutputStreamWriter writer = null;
            try {
                fout = new FileOutputStream(jsonFile);
                writer = new OutputStreamWriter(fout, "utf8");
                writer.write(jsonStr, 0, jsonStr.length());
            } catch (IOException e) {
                showError(e.toString());
                return;
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    } else if (fout != null) {
                        fout.close();
                    }
                } catch (IOException e) {
                    // ignore
                }
            }
            showResult("Success", "JSON written to file: " + jsonFile.getAbsolutePath());
        }

        private void showFragmentDialog(int id, String messageTitle, String messageText) {
            Bundle b = new Bundle();
            b.putInt(DIALOG_ID_KEY, id);
            b.putString(DIALOG_TEXT_KEY, messageText);
            b.putString(DIALOG_TITLE_KEY, messageTitle);

            idf.setArguments(b);
            idf.show(getActivity().getSupportFragmentManager(), "ItemDialogFragment");
        }

        /**
         * call the SDK API with the selected identifier.
         *
         * @param deviceInfoId
         * @return the string, containing the requested data
         */
        private String getDeviceInfo(int deviceInfoId) {
            String info = "null";

            MobileAPI mobileAPI = MobileAPI.getInstance(getActivity());
            tryCollectInfo(mobileAPI);

            if (mJDeviceInfo == null) {
                return info;
            }

            JSONObject temp;
            if ("WiFiNetworksData".equals(mJSONKey[deviceInfoId])) {
                try {
                    temp = mJDeviceInfo.getJSONObject(mJSONKey[deviceInfoId]);
                    info = jNetworkDataToString(temp);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            } else if ("GeoLocationInfo".equals(mJSONKey[deviceInfoId])) {
                // Refresh info to get the newest location
                String jstr = mobileAPI.collectInfo();
                try {
                    mJDeviceInfo = new JSONObject(jstr);
                    JSONArray atemp = mJDeviceInfo.getJSONArray(mJSONKey[deviceInfoId]);
                    temp = atemp.getJSONObject(0);
                    info = jLocationToString(temp);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            } else {
                try {
                    if(!mJDeviceInfo.isNull(mJSONKey[deviceInfoId])){
                        info = mJDeviceInfo.getString(mJSONKey[deviceInfoId]);
                    }
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            }
            return info;
        }

        private Properties getSdkProperties() {
            Properties properties = new Properties();

            properties.setProperty(MobileAPI.CONFIGURATION_KEY, "" + configuration);
            properties.setProperty(MobileAPI.TIMEOUT_MINUTES_KEY, "" + TIMEOUT_VALUE);
            properties.setProperty(MobileAPI.BEST_LOCATION_AGE_MINUTES_KEY, "" + BEST_LOCATION_AGE_MINUTES_VALUE);
            properties.setProperty(MobileAPI.MAX_LOCATION_AGE_DAYS_KEY, "" + MAX_LOCATION_AGE_DAYS_VALUE);
            properties.setProperty(MobileAPI.ADD_TIMESTAMP_KEY, "1");
            // override max accuracy - it is 100 meter by default, but we get
            // network location with higher
            // accuracy, we want to force GPS locations
            properties.setProperty(MobileAPI.MAX_ACCURACY_KEY, "" + 50);

            return properties;
        }

        @Override
        public void onResume() {
            super.onResume();

			/*
			 * Start the data collection query on the background
			 */
            MobileAPI mobileAPI = MobileAPI.getInstance(getActivity());
            Log.d(TAG, "onResume - Calling initSDK");
            mobileAPI.initSDK(getSdkProperties());
        }

        @Override
        public void onPause() {
            super.onPause();
            MobileAPI mobileAPI = MobileAPI.getInstance(getActivity());
            Log.d(TAG, "onResume - Calling destroy");
            mobileAPI.destroy();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            // SDK is now destroyed in onPause. We stop collecting information
            // when app goes to the background. Since the test app is only
            // a single activity, this is ok. For more complex applications,
            // the concept of "going to the background" is more complex to
            // handle,
            // since a single activity of the application may be paused but
            // another
            // activity is started, in which case the app is still in the
            // foreground
            // and data collection should not be stopped.
            // MobileAPI mobileAPI = MobileAPI.getInstance(this);
            // mobileAPI.destroy();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putString(TITLE_KEY, mTitleStr);
            outState.putString(RESULT_KEY, mResultStr);
        }

        /**
         * show error dialog
         */
        private void showError(String msg) {
            idf.dismiss();
            showFragmentDialog(ERROR_DIALOG, mTitleStr, msg);
        }

        /**
         * show result dialog
         */
        private void showResult(String title, String msg) {
            showFragmentDialog(IMMEDIATE_RESULT_DIALOG, title, msg);
        }

        private void tryCollectInfo(MobileAPI mobileAPI) {
			/*
			 * Get the collected info
			 */
            String deviceInfoStr = mobileAPI.collectInfo();
            try {
                mJDeviceInfo = new JSONObject(deviceInfoStr);
            } catch (JSONException je) {
                je.printStackTrace();
            }
        }

        private String jLocationToString(JSONObject jobj) {
            StringBuffer buffer = new StringBuffer();
            for (String s : mJSONLocationKey) {
                String value = null;
                try {
                    value = jobj.getString(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // otherwise ignore...
                }
                if (value != null) {
                    buffer.append(s);
                    buffer.append(":\n");
                    buffer.append(value);
                    buffer.append("\n");
                }
            }
            return buffer.toString();
        }

        private String jNetworkDataToString(JSONObject jobj) {
            StringBuffer buffer = new StringBuffer();
            for (String s : mJSONNetworksDataKey) {
                String value = null;
                try {
                    value = jobj.getString(s);
                } catch (JSONException e) {
                    // ....
                }
                if (value != null) {
                    buffer.append(s);
                    buffer.append(":\n");
                    buffer.append(value);
                    buffer.append("\n");
                }
            }
            return buffer.toString();
        }
    }

    /**
     //	 * Requesting permissions based on the mode
     //	 * @param collectionMode
     //	 */
	public void requestPermissions(int collectionMode){
		List<String> permissionsList = new ArrayList<>();
		List<String>  permissionsNeeded = new ArrayList<>();


		if(collectionMode >= MobileAPI.COLLECT_BASIC_DEVICE_DATA_ONLY){
			if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE)){
				permissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
			}
		}


		if(collectionMode >= MobileAPI.COLLECT_ALL_DEVICE_DATA_AND_LOCATION){
			if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)){
				permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			}
			if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)){
				permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
			}
		}

		if(!permissionsList.isEmpty()){
			ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
		}

	}

    private boolean addPermission(List<String> permissionsList, String permission) {
		if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
			permissionsList.add(permission);
			if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
				return false;
			}
		}
		return true;
	}
}