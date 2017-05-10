package genrozun.droshed;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class DataManager {

    /**
     * Returns the File of the data version of the current logged user for the given model name
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     * @param version the asked data version
     * @return the asked file
     */
    public File getDataVersion(Context context, String model, int version) {
        SharedPreferences logins = context.getSharedPreferences("droshed_logins", Context.MODE_PRIVATE);
        String user = logins.getString("droshed_user", null);
        if(user == null) throw new IllegalStateException("User can't be null");

        File userDataFolder = new File(getModelDataFolder(context, model), user);
        if(!userDataFolder.exists()) if(!userDataFolder.mkdir()) throw new IllegalStateException("Couldn't create user data folder");

        File data = new File(userDataFolder, ""+version);
        if(!userDataFolder.exists()) throw new IllegalStateException("Data version n°"+version+"doesn't exist for the user/model couple "+user+"/"+model);

        return data;
    }

    /**
     * Returns the folder containing the models file.
     * @param context the application Context giving access to SharedPreferences
     * @return
     */
    private File getModelsRootFolder(Context context) {
        File modelRoot = new File(context.getFilesDir(), "models");
        if(!modelRoot.exists()) if(!modelRoot.mkdir()) throw new IllegalStateException("Couldn't create root models folder");

        return modelRoot;
    }

    /**
     * Returns the folder containing all models data folders
     * @param context the application Context giving access to SharedPreferences
     * @return
     */
    private File getModelDataRootFolder(Context context) {
        File dataRoot = new File(context.getFilesDir(), "data");
        if(!dataRoot.exists()) if(!dataRoot.mkdir()) throw new IllegalStateException("Couldn't create root data folder");

        return dataRoot;
    }

    /**
     * Returns the folder containing all users data folders for the given model
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     * @return
     */
    private File getModelDataFolder(Context context, String model) {
        File dataFolder = new File(getModelDataRootFolder(context), model);
        if(!dataFolder.exists()) if(!dataFolder.mkdir()) throw new IllegalStateException("Couldn't create model data folder");
        return dataFolder;
    }

    /**
     * Returns the last version number for the current logged user and given model name
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     * @return
     */
    private int getLastVersionNumber(Context context, String model) {
        SharedPreferences logins = context.getSharedPreferences("droshed_logins", Context.MODE_PRIVATE);
        String user = logins.getString("droshed_user", null);
        if(user == null) throw new IllegalStateException("User can't be null");

        SharedPreferences modelMetadata = context.getSharedPreferences("droshed_model_"+model, Context.MODE_PRIVATE);
        int lastVersion = modelMetadata.getInt(user+"_lastVersion", 0);
        if(lastVersion == 0) throw new IllegalStateException("Wrong metadata for model "+model);

        return lastVersion;
    }

    /**
     * Increments the data version number for the current logged user and given model name
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     */
    private void incrementLastVersionNumber(Context context, String model) {
        SharedPreferences logins = context.getSharedPreferences("droshed_logins", Context.MODE_PRIVATE);
        String user = logins.getString("droshed_user", null);
        if(user == null) throw new IllegalStateException("User can't be null");

        SharedPreferences modelMetadata = context.getSharedPreferences("droshed_model_"+model, Context.MODE_PRIVATE);

        int lastVersion = modelMetadata.getInt(user+"_lastVersion", 0);
        if(lastVersion == 0) throw new IllegalStateException("Wrong metadata for model "+model);

        SharedPreferences.Editor editor = modelMetadata.edit();
        editor.putInt(user+"_lastVersion", lastVersion+1);
        editor.apply();
    }

    /**
     * Returns the last version data for the current logged user and given model name
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     * @return
     */
    public File getLastVersionData(Context context, String model) {
        return getDataVersion(context, model, getLastVersionNumber(context, model));
    }

    /**
     * Returns the model schema file for the given model name
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     * @return
     */
    public File getModel(Context context, String model) {
        File modelFile = new File(getModelsRootFolder(context), model);
        if(!modelFile.exists()) throw new IllegalStateException("Model "+model+" doesn't exist");

        return modelFile;
    }

    /**
     * Creates a new model with the given name, containing the given content
     * @param context the application Context giving access to SharedPreferences
     * @param modelName the model name
     * @param modelContent the new model content
     */
    public void createModel(Context context, String modelName, String modelContent) {
        File modelFile = new File(getModelsRootFolder(context), modelName);

        try {
            if(!modelFile.createNewFile()) throw new IllegalStateException("Couldn't create new file to save the model");
            FileWriter writer = new FileWriter(modelFile);
            writer.write(modelContent);
            writer.close();
        } catch(IOException e) {
            throw new IllegalStateException("Can't create new file to save the model, or write model schema to it");
        }
    }

    /**
     * Create a new data version for the current logged user and given model name, with the given content
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     * @param newContent the new data content
     */
    public void createNewVersion(Context context, String model, String newContent) {
        SharedPreferences logins = context.getSharedPreferences("droshed_logins", Context.MODE_PRIVATE);
        String user = logins.getString("droshed_user", null);
        if(user == null) throw new IllegalStateException("User can't be null");

        File userDataFolder = new File(getModelDataFolder(context, model), user);
        if(!userDataFolder.exists()) if(!userDataFolder.mkdir()) throw new IllegalStateException("Couldn't create user data folder");

        int lastVersion = getLastVersionNumber(context, model);
        File data = new File(userDataFolder, ""+(lastVersion+1));

        try {
            FileWriter writer = new FileWriter(data);
            writer.write(newContent);
            writer.close();
            incrementLastVersionNumber(context, model);
        } catch (IOException e) {
            throw new IllegalStateException("Can't create new file to save the new version, or write data");
        }
    }
}