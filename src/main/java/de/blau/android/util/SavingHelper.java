package de.blau.android.util;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.acra.ACRA;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import de.blau.android.R;

/**
 * Helper class for loading and saving individual serializable objects to files.
 * Does all error handling, stream opening etc.
 *
 * @param <T> The type of the saved objects
 */
public class SavingHelper<T extends Serializable> {

	private static final String DEBUG_TAG = SavingHelper.class.getSimpleName();
	
	/**
	 * Date pattern used for the export file name.
	 */
	private static final String DATE_PATTERN_EXPORT_FILE_NAME_PART = "yyyy-MM-dd'T'HHmmss";

	/**
	 * Serializes the given object and writes it to a private file with the given name
	 * 
	 * Original version was running out of stack, fixed by moving to a thread
	 * 
	 * @param filename filename of the save file
	 * @param object object to save
	 * @param compress true if the output should be gzip-compressed, false if it should be written without compression
	 * @return true if successful, false if saving failed for some reason
	 */
	public synchronized boolean save(Context context, String filename, T object, boolean compress) {
		
		try
		{
			Log.d("SavingHelper", "preparing to save " + filename);
			SaveThread r = new SaveThread(context, filename, object, compress);
			Thread t = new Thread(null, r, "SaveThread", 200000);
			t.start();
			t.join(60000); // wait max 60 s for thread to finish TODO this needs to be done differently given this limits the size of the file that can be saved
			Log.d("SavingHelper", "save thread finished");
			return r.getResult();
		} catch (Exception e) {
			ACRA.getErrorReporter().putCustomData("STATUS","NOCRASH");
			ACRA.getErrorReporter().handleException(e); // serious error report if we has crashed
			return false;
		}
	}
		
	public class SaveThread implements Runnable {
		
		final String filename;
		T	object;
		final boolean compress;
		final Context context;
		boolean result = false;
		
		SaveThread(Context context, String fn, T obj, boolean c) {
			filename = fn;
			object = obj;
			compress = c;
			this.context = context;
		}
		
		public boolean getResult() {
			return result;
		}
		
		@Override
		public void run() {

        	OutputStream out = null;
        	ObjectOutputStream objectOut = null;
        	try {
        		Log.i("SavingHelper", "saving  " + filename);
        		String tempFilename = filename + "." + System.currentTimeMillis();
        		out = context.openFileOutput(tempFilename, Context.MODE_PRIVATE);
        		if (compress) {
        			out = new GZIPOutputStream(out);
        		}
        		objectOut = new ObjectOutputStream(out);
        		objectOut.writeObject(object);
        		rename(context, filename, filename + ".backup"); // don't overwrite last saved state
        		rename(context, tempFilename, filename); 		 // rename to expected name 
        		Log.i("SavingHelper", "saved " + filename + " successfully");
        		result = true;
        	} catch (Exception e) {
        		Log.e("SavingHelper", "failed to save "+filename, e);
        		ACRA.getErrorReporter().putCustomData("STATUS","NOCRASH");
        		ACRA.getErrorReporter().handleException(e); // serious error report as if we had crashed
        		result = false;
        	} catch (Error e) {
        		Log.e("SavingHelper", "failed to save "+filename, e);
        		ACRA.getErrorReporter().putCustomData("STATUS","NOCRASH");
        		ACRA.getErrorReporter().handleException(e); // serious error report as if we had crashed
        		result = false;
        	} finally {
        		SavingHelper.close(objectOut);
        		SavingHelper.close(out);
        	}
        }
	}
	
	/**
	 * Loads and deserializes a single object from the given file
	 * Original version was running out of stack, fixed by moving to a thread
	 *  
	 * @param filename filename of the save file
	 * @param compressed true if the output is gzip-compressed, false if it is uncompressed
	 * @return the deserialized object if successful, null if loading/deserialization/casting failed
	 */
	public synchronized T load(Context context, String filename, boolean compressed) {
		return load(context, filename, compressed, false);
	}
	
	private synchronized T load(Context context, String filename, boolean compressed, boolean deleteOnFail) {
		try
		{
			Log.d("SavingHelper", "preparing to load " + filename);
			LoadThread r = new LoadThread(context, filename, compressed, deleteOnFail);
			Thread t = new Thread(null, r, "LoadThread", 200000);
			t.start();
			t.join(60000); // wait max 60 s for thread to finish TODO this needs to be done differently given this limits the size of the file that can be loaded
			Log.d("SavingHelper", "load thread finished");
			return r.getResult();
		} catch (Exception e) {
			ACRA.getErrorReporter().putCustomData("STATUS","NOCRASH");
			ACRA.getErrorReporter().handleException(e); // serious error report as if we had crashed
			return null;
		}
	}
	
	public class LoadThread implements Runnable {
		
		final String filename;
		final boolean compressed;
		final boolean deleteOnFail;
		final Context context;
		T result;
		
		LoadThread(Context context, String fn,  boolean c, boolean deleteOnFail) {
			filename = fn;
			compressed = c;
			this.deleteOnFail = deleteOnFail;
			this.context = context;
		}
		
		public T getResult() {
			return result;
		}
		
		@Override
		public void run() {

			InputStream in = null;
			ObjectInputStream objectIn = null;
			try {
				Log.d("SavingHelper", "loading  " + filename);
				try {
					in = context.openFileInput(filename);
				} catch (FileNotFoundException fnfe) {
					// this happens a lot and shouldn't generate an error report
					Log.e("SavingHelper", "file not found " + filename);
					result = null;
					return;
				}
				if (compressed) {
					in = new GZIPInputStream(in);
				}
				objectIn = new ObjectInputStream(in);
				@SuppressWarnings("unchecked") // casting exceptions are caught by the exception handler
				T object = (T) objectIn.readObject();
				Log.d("SavingHelper", "loaded " + filename + " successfully");
				result = object;
			} catch (IOException ioex) {
				Log.e("SavingHelper", "failed to load " + filename, ioex);
				try {
					if (deleteOnFail) {
						context.deleteFile(filename);
					}
				} catch (Exception ex) {
					// ignore
				}
				result = null;
			} catch (Exception e) {
				Log.e("SavingHelper", "failed to load " + filename, e);
				result = null;
				if (e instanceof InvalidClassException) { // serial id mismatch, will typically happen on upgrades
					// do nothing 
				} else {
					ACRA.getErrorReporter().putCustomData("STATUS","NOCRASH");
					ACRA.getErrorReporter().handleException(e); //
				}
			} catch (Error e) {
				Log.e("SavingHelper", "failed to load " + filename, e);
				result = null;
				ACRA.getErrorReporter().putCustomData("STATUS","NOCRASH");
				ACRA.getErrorReporter().handleException(e); // 
			} finally {
				SavingHelper.close(objectIn);
				SavingHelper.close(in);
			}
		}
	}

	
	/**
	 * Convenience function - closes the given stream (can be any Closable), catching and logging exceptions
	 * @param stream a Closeable to close
	 */
	static public void close(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Log.e("SavingHelper", "Problem closing", e);
			}
		}
	}
	
	/**
	 * Rename existing file in same directory if target file exists, delete
	 * Code nicked from http://stackoverflow.com/users/325442/mr-bungle
	 * @param context
	 * @param originalFileName
	 * @param newFileName
	 */
	private static void rename(Context context, String originalFileName, String newFileName) {
	    File originalFile = context.getFileStreamPath(originalFileName);
	    if (originalFile.exists()) {
	    	Log.d("SavingHelper", "renaming " + originalFileName + " size " + originalFile.length() + " to " + newFileName);
	    	File newFile = new File(originalFile.getParent(), newFileName);
	    	if (newFile.exists()) {
	    		context.deleteFile(newFileName);        
	    	}
	    	originalFile.renameTo(newFile);
	    }
	}

	/**
	 * Exports an Exportable asynchronously, displaying a toast on success or failure
	 * 
	 * @param ctx context for the toast
	 * @param exportable the exportable to run
	 */
	public static void asyncExport(@NonNull final Context ctx, @NonNull final Exportable exportable) {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {

				String filename = DateFormatter
						.getFormattedString(DATE_PATTERN_EXPORT_FILE_NAME_PART) +
						"." + exportable.exportExtension();

				OutputStream outputStream = null;
				File outfile = null;
				FileOutputStream fout = null;
				try {
					File outDir = FileUtil.getPublicDirectory();
					outfile = new File(outDir, filename);
					fout = new FileOutputStream(outfile);
					outputStream = new BufferedOutputStream(fout);
					exportable.export(outputStream);
				} catch (Exception e) {
					Log.e("SavingHelper", "Export failed - " + filename);
					return null;
				} finally {
					SavingHelper.close(outputStream);
					SavingHelper.close(fout);
				}
				// workaround for android bug - make sure export file shows up via MTP
				if (ctx != null && outfile != null){
					try {
						triggerMediaScanner(ctx, outfile);
					} catch (Exception ignored) {
						Log.e(DEBUG_TAG,"Toast in asyncExport failed with " + ignored.getMessage());
					} catch (Error ignored) {
						Log.e(DEBUG_TAG,"Toast in asyncExport failed with " + ignored.getMessage());
					}
				}
				return filename;
			}

			@Override
			protected void onPostExecute(String result) {
				if (ctx != null) {
					try {
						if (ctx instanceof Activity) {
							if (result == null) {
								Snack.barError((Activity)ctx, R.string.toast_export_failed);
							} else {
								Log.i("SavingHelper", "Successful export to " + result);
								String text = ctx.getResources().getString(R.string.toast_export_success, result);
								Snack.barInfoShort((Activity)ctx, text);
							}
						}
					} catch (Exception ignored) {
						Log.e(DEBUG_TAG,"Toast in asyncExport.onPostExecute failed with " + ignored.getMessage());
					} catch (Error ignored) {
						Log.e(DEBUG_TAG,"Toast in asyncExport.onPostExecute failed with " + ignored.getMessage());
					}
				}
			}
		}.execute();
	}

	/**
	 * Trigger the media scanner to ensure files show up in MTP.
	 * @param context a context to use for communication with the media scanner
	 * @param scanfile directory or file to scan
	 */
	@TargetApi(11)
	private static void triggerMediaScanner(Context context, File scanfile) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) return; // API 11 - lower versions do not have MTP
		try {
			String path = scanfile.getCanonicalPath();
			Log.i("SavingHelper", "Triggering media scan for " + path);
			MediaScannerConnection.scanFile(context, new String[] {path}, null, new OnScanCompletedListener() {
				@Override
				public void onScanCompleted(String path, Uri uri) {
					Log.i("SavingHelper", "Media scan completed for " + path + " URI " + uri);
				}				
			});
		} catch (Exception e) {
			Log.e("SavingHelper", "Exception when triggering media scanner", e);
		}
	}


	public interface Exportable {
		/** Exports some data to an OutputStream */
		void export(OutputStream outputStream) throws Exception;
		
		/** @returns the extension to be used for exports */
		String exportExtension();
	}
	
}
