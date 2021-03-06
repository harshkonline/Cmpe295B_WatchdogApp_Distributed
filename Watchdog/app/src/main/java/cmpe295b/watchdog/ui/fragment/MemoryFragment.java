package cmpe295b.watchdog.ui.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;

import cmpe295b.watchdog.R;
import cmpe295b.watchdog.core.WatchdogApplication;
import cmpe295b.watchdog.ui.activity.BaseActivity;
import cmpe295b.watchdog.ui.graph.PieGraph;
import cmpe295b.watchdog.ui.graph.PieSlice;
import cmpe295b.watchdog.util.UiUtils;
import timber.log.Timber;


public class MemoryFragment extends Fragment {

    private PieGraph pg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_memory, container, false);
        // Get tracker.
        Tracker t = ((WatchdogApplication) getActivity().getApplication()).getTracker(WatchdogApplication.TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName("MemoryFragment");
        t.send(new HitBuilders.ScreenViewBuilder().build());

        //get elements
        pg = (PieGraph) view.findViewById(R.id.graph);
        //ram
        TextView textViewRamTotalLabel = (TextView) view.findViewById(R.id.textview_memory_used_ram_label);
        TextView textViewRamUsedLabel = (TextView) view.findViewById(R.id.textview_memory_used_ram_label);
        TextView textViewRamTotal = (TextView) view.findViewById(R.id.textview_memory_total_ram);
        TextView textViewRamAvailable = (TextView) view.findViewById(R.id.textview_memory_available_ram);
        TextView textViewRamPercentageLabel = (TextView) view.findViewById(R.id.textview_memory_percentage_ram_label);
        TextView textviewRamPercentage= (TextView) view.findViewById(R.id.textview_memory_percentage_ram);

        TextView textViewRamUsed = (TextView) view.findViewById(R.id.textview_memory_used_ram);
        TextView textViewRamLow = (TextView) view.findViewById(R.id.textview_memory_low_ram);
        TextView textViewRamThreshold = (TextView) view.findViewById(R.id.textview_memory_threshold_ram);
        //storage
        TextView textViewStorageAvailableExternal = (TextView) view.findViewById(R.id.textview_memory_available_external);
        TextView textViewStorageAvailableInternal = (TextView) view.findViewById(R.id.textview_memory_available_internal);
        TextView textViewStorageExternalStoragePresent = (TextView) view.findViewById(R.id.textview_memory_external_storage);
        TextView textViewStorageTotalExternal = (TextView) view.findViewById(R.id.textview_memory_total_external);
        TextView textViewStorageTotalInternal = (TextView) view.findViewById(R.id.textview_memory_total_internal);



        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        if (Build.VERSION.SDK_INT >= 16) {
            // percentage can be calculated for API 16+
            Long percentageAvailaible = (long) ((float) mi.availMem / mi.totalMem * 100);

            Timber.d("percent available : " + percentageAvailaible + "%");
            Timber.d("Total memory" + UiUtils.humanReadableByteCount(mi.totalMem, true));

            PieSlice slice = new PieSlice();
            slice.setTitle(getString(R.string.memory_used_ram));
            slice.setColor(Color.parseColor("#AA66CC"));
            slice.setValue(mi.totalMem - mi.availMem);
            pg.addSlice(slice);
            slice = new PieSlice();
            slice.setTitle(getString(R.string.memory_available_ram));
            slice.setColor(Color.parseColor("#FFBB33"));
            slice.setValue(mi.availMem);
            pg.addSlice(slice);

            //ram
            textViewRamTotal.setText(UiUtils.humanReadableByteCount(mi.totalMem, true));
            textViewRamUsed.setText(UiUtils.humanReadableByteCount(mi.totalMem - mi.availMem, true));
            textviewRamPercentage.setText(percentageAvailaible + " %");
        }
        else {
            pg.setVisibility(View.GONE);
            textViewRamPercentageLabel.setVisibility(View.GONE);
            textviewRamPercentage.setVisibility(View.GONE);
            textViewRamTotalLabel.setVisibility(View.GONE);
            textViewRamUsedLabel.setVisibility(View.GONE);
            textViewRamTotal.setVisibility(View.GONE);
            textViewRamUsed.setVisibility(View.GONE);
        }


        //set elements
        //ram
        textViewRamAvailable.setText(UiUtils.humanReadableByteCount(mi.availMem, true));
        textViewRamLow.setText(Boolean.toString(mi.lowMemory));
        textViewRamThreshold.setText(UiUtils.humanReadableByteCount(mi.threshold, true));
        //storage
        textViewStorageAvailableExternal.setText(getAvailableExternalMemorySize());
        textViewStorageAvailableInternal.setText(getAvailableInternalMemorySize());
        textViewStorageExternalStoragePresent.setText(Boolean.toString(externalMemoryAvailable()));
        textViewStorageTotalExternal.setText(getTotalExternalMemorySize());
        textViewStorageTotalInternal.setText(getTotalInternalMemorySize());


        //System
        Timber.d("Available memory " + UiUtils.humanReadableByteCount(mi.availMem, true));
        Timber.d("Low memory " + mi.lowMemory);
        Timber.d("Threshold memory " + UiUtils.humanReadableByteCount(mi.threshold, true));

        //Storage
        Timber.d("External memory : " + externalMemoryAvailable());
        Timber.d("Available internal memory size : " + getAvailableInternalMemorySize());
        Timber.d("Total internal memory size : " + getTotalInternalMemorySize());
        Timber.d("Available external memory size : " + getAvailableExternalMemorySize());
        Timber.d("Total external memory size : " + getTotalExternalMemorySize());


        return view;
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "Not found";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return "Not found";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = " KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = " MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((BaseActivity) activity).onSectionAttached(5);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
