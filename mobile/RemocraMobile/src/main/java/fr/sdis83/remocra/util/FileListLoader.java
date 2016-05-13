package fr.sdis83.remocra.util;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jean-Philippe on 07/01/2016.
 * Un loader chargeant les fichiers "backup" de l'application
 * Fortement inspire de http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
 */
public class FileListLoader extends AsyncTaskLoader<List<File>> {

    private final File directory;
    private List<File> files;

    public FileListLoader(Context context, File directory) {
        super(context);
        this.directory = directory;
        if (this.directory.exists()) {
            this.directory.mkdirs();
        }
    }

    /****************************************************/
    /** (1) A task that performs the asynchronous load **/
    /****************************************************/

    public List<File> loadInBackground() {
        List<File> files =  Arrays.asList(this.directory.listFiles());
        // TODO : sort collections by date desc
        return files;
    }

    /********************************************************/
    /** (2) Deliver the results to the registered listener **/
    /********************************************************/

    @Override
    public void deliverResult(List<File> data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<File> oldData = this.files;
        this.files = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    /*********************************************************/
    /** (3) Implement the Loader's state-dependent behavior **/
    /*********************************************************/

    @Override
    protected void onStartLoading() {
        if (this.files != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(this.files);
        }

        if (takeContentChanged() || this.files == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (this.files != null) {
            releaseResources(this.files);
            this.files = null;
        }
    }

    @Override
    public void onCanceled(List<File> data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(List<File> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }
}
