package com.amaze.filemanager.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.amaze.filemanager.database.ExplorerDatabase;
import com.amaze.filemanager.database.UtilitiesDatabase;
import com.amaze.filemanager.database.UtilsHandler;
import com.amaze.filemanager.fileoperations.exceptions.ShellNotRunningException;
import com.amaze.filemanager.fileoperations.filesystem.OpenMode;
import com.amaze.filemanager.filesystem.HybridFile;
import com.amaze.filemanager.filesystem.ssh.CustomSshJConfig;
import com.amaze.filemanager.ui.fragments.preferencefragments.PreferencesConstants;
import com.amaze.filemanager.ui.provider.UtilitiesProvider;
import com.amaze.trashbin.TrashBin;
import com.amaze.trashbin.TrashBinConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.ref.WeakReference;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jcifs.Config;
import jcifs.smb.SmbException;

public class AmazeFileManagerApplication extends GlideApplication {

    private static final String TRASH_BIN_BASE_PATH =
            Environment.getExternalStorageDirectory().getPath() + File.separator + ".AmazeData";
    private static AmazeFileManagerApplication instance;
    private Logger log = null;
    private UtilitiesProvider utilsProvider;
    private UtilsHandler utilsHandler;
    private WeakReference<Context> mainActivityContext;
    private ExplorerDatabase explorerDatabase;
    private TrashBinConfig trashBinConfig;
    private TrashBin trashBin;

    /**
     * Shows a toast message
     *
     * @param context Any context belonging to this application
     * @param message The message to show
     */
    public static void toast(Context context, @StringRes int message) {
        // this is a static method so it is easier to call,
        // as the context checking and casting is done for you

        if (context == null) return;

        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }

        if (context instanceof Application) {
            final Context c = context;
            final @StringRes int m = message;

            getInstance().runInApplicationThread(() -> Toast.makeText(c, m, Toast.LENGTH_LONG).show());
        }
    }

    /**
     * Shows a toast message
     *
     * @param context Any context belonging to this application
     * @param message The message to show
     */
    public static void toast(Context context, String message) {
        // this is a static method so it is easier to call,
        // as the context checking and casting is done for you

        if (context == null) return;

        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }

        if (context instanceof Application) {
            final Context c = context;
            final String m = message;

            getInstance().runInApplicationThread(() -> Toast.makeText(c, m, Toast.LENGTH_LONG).show());
        }
    }

    public static synchronized AmazeFileManagerApplication getInstance() {
        return instance;
    }

    public UtilitiesProvider getUtilsProvider() {
        return utilsProvider;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(
                true); // selector in srcCompat isn't supported without this
        instance = this;

        CustomSshJConfig.init();
        explorerDatabase = ExplorerDatabase.initialize(this);
        UtilitiesDatabase utilitiesDatabase = UtilitiesDatabase.initialize(this);

        utilsProvider = new UtilitiesProvider(this);
        utilsHandler = new UtilsHandler(this, utilitiesDatabase);

        runInBackground(Config::registerSmbURLHandler);

        // disabling file exposure method check for api n+
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        log = LoggerFactory.getLogger(AmazeFileManagerApplication.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Post a runnable to handler. Use this in case we don't have any restriction to execute after
     * this runnable is executed, and {#runInBackground(Runnable)} in case we need to execute
     * something after execution in background.
     */
    public void runInBackground(Runnable runnable) {
        Completable.fromRunnable(runnable).subscribeOn(Schedulers.io()).subscribe();
    }

    /**
     * Run a {@link Runnable} in the main application thread
     *
     * @param r {@link Runnable} to run
     */
    public void runInApplicationThread(@NonNull Runnable r) {
        Completable.fromRunnable(r).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    public UtilsHandler getUtilsHandler() {
        return utilsHandler;
    }

    @Nullable
    public Context getMainActivityContext() {
        return mainActivityContext.get();
    }

    public void setMainActivityContext(@NonNull Activity activity) {
        mainActivityContext = new WeakReference<>(activity);
    }

    public ExplorerDatabase getExplorerDatabase() {
        return explorerDatabase;
    }

    public TrashBin getTrashBinInstance() {
        if (trashBin == null) {
            trashBin =
                    new TrashBin(
                            getApplicationContext(),
                            true,
                            getTrashBinConfig(),
                            s -> {
                                runInBackground(
                                        () -> {
                                            HybridFile file = new HybridFile(OpenMode.TRASH_BIN, s);
                                            try {
                                                file.delete(getMainActivityContext(), false);
                                            } catch (ShellNotRunningException | SmbException e) {
                                                log.warn("failed to delete file in trash bin cleanup", e);
                                            }
                                        });
                                return true;
                            },
                            null
                    );
        }
        return trashBin;
    }

    private TrashBinConfig getTrashBinConfig() {
        if (trashBinConfig == null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            int days =
                    sharedPrefs.getInt(
                            PreferencesConstants.KEY_TRASH_BIN_RETENTION_DAYS,
                            TrashBinConfig.RETENTION_DAYS_INFINITE
                    );
            long bytes =
                    sharedPrefs.getLong(
                            PreferencesConstants.KEY_TRASH_BIN_RETENTION_BYTES,
                            TrashBinConfig.RETENTION_BYTES_INFINITE
                    );
            int numOfFiles =
                    sharedPrefs.getInt(
                            PreferencesConstants.KEY_TRASH_BIN_RETENTION_NUM_OF_FILES,
                            TrashBinConfig.RETENTION_NUM_OF_FILES
                    );
            int intervalHours =
                    sharedPrefs.getInt(
                            PreferencesConstants.KEY_TRASH_BIN_CLEANUP_INTERVAL_HOURS,
                            TrashBinConfig.INTERVAL_CLEANUP_HOURS
                    );
            trashBinConfig =
                    new TrashBinConfig(
                            TRASH_BIN_BASE_PATH, days, bytes, numOfFiles, intervalHours, false, true);
        }
        return trashBinConfig;
    }
}
