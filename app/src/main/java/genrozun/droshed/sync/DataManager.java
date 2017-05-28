package genrozun.droshed.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class DataManager {

    private static File getModelsListFile(Context context) {
        String user = checkUser(context);

        File modelsList = new File(context.getFilesDir(), user+"_modellist");
        if(!modelsList.exists()) try {
            Log.i("DATAMANAGER", "File wasn't existing. Creating new one...");
            if(!modelsList.createNewFile()) throw new IllegalStateException("Couldn't create user model list file");
            setNewModelsList(context, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  modelsList;
    }

    public static void setNewModelsList(Context context, ArrayList<String> newList) {
         try {
             ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(getModelsListFile(context), false));
             out.writeObject(newList);
             out.close();
             Log.i("DATAMANAGER", "New version written to models file: "+newList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addModelToModelsList(Context context, String modelToAdd) {
        ArrayList<String> models = DataManager.getModelsList(context);
        models.add(modelToAdd);
        DataManager.setNewModelsList(context, models);
    }

    public static void deleteModelFromModelsList(Context context, String modelToRemove) {
        ArrayList<String> models = getModelsList(context);
        models.remove(modelToRemove);
        setNewModelsList(context, models);

        // Pour le moment aucune suppression des fichiers locaux
    }

    public static ArrayList<String> getModelsList(Context context) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(getModelsListFile(context)));
            ArrayList<String> list = (ArrayList<String>) in.readObject();
            in.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Returns the File of the data version of the current logged user for the given model name
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     * @param version the asked data version
     * @return the asked file
     */
    public static File getDataVersion(Context context, String model, int version) {
        String user = checkUser(context);

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
    private static File getModelsRootFolder(Context context) {
        File modelRoot = new File(context.getFilesDir(), "models");
        if(!modelRoot.exists()) if(!modelRoot.mkdir()) throw new IllegalStateException("Couldn't create root models folder");

        return modelRoot;
    }

    /**
     * Returns the folder containing all models data folders
     * @param context the application Context giving access to SharedPreferences
     * @return
     */
    private static File getModelDataRootFolder(Context context) {
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
    private static File getModelDataFolder(Context context, String model) {
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
    public static int getLastVersionNumberForModel(Context context, String model) {
        String user = checkUser(context);

        SharedPreferences modelMetadata = context.getSharedPreferences("droshed_model_"+model, Context.MODE_PRIVATE);
        int lastVersion = modelMetadata.getInt(user+"_lastVersion", -1);
        if(lastVersion == -1) throw new IllegalStateException("Wrong metadata for model "+model);

        Log.i("DATAMANAGER", "Last data version for model "+model+" is "+lastVersion);
        return lastVersion;
    }

    /**
     * Increments the data version number for the current logged user and given model name
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     */
    private static void incrementLastVersionNumber(Context context, String model) {
        String user = checkUser(context);

        SharedPreferences modelMetadata = context.getSharedPreferences("droshed_model_"+model, Context.MODE_PRIVATE);

        int lastVersion = modelMetadata.getInt(user+"_lastVersion", -1);
        if(lastVersion == -1) throw new IllegalStateException("Wrong metadata for model "+model);

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
    public static File getLastVersionData(Context context, String model) {
        return getDataVersion(context, model, getLastVersionNumberForModel(context, model));
    }

    /**
     * Returns the model schema file for the given model name
     * @param context the application Context giving access to SharedPreferences
     * @param model the model name
     * @return
     */
    public static File getModel(Context context, String model) {
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
    public static void createModel(Context context, String modelName, String modelContent) {
        File modelFile = new File(getModelsRootFolder(context), modelName);
        String user = checkUser(context);

        try {
            Log.i("DATAMANAGER", "Creating new model file: "+modelFile.getAbsolutePath());
            FileOutputStream outStream = new FileOutputStream(modelFile);

            OutputStreamWriter writer = new OutputStreamWriter(outStream);
            writer.write(modelContent);
            writer.close();


            SharedPreferences modelMetadata = context.getSharedPreferences("droshed_model_"+modelName, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = modelMetadata.edit();
            editor.putInt(user+"_lastVersion", 0);
            editor.apply();

            addModelToModelsList(context, modelName);

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
    public static void createNewVersion(Context context, String model, String newContent) {
        String user = checkUser(context);

        File userDataFolder = new File(getModelDataFolder(context, model), user);
        if(!userDataFolder.exists()) if(!userDataFolder.mkdir()) throw new IllegalStateException("Couldn't create user data folder");

        int lastVersion = getLastVersionNumberForModel(context, model);
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

    private static String checkUser(Context context) {
        SharedPreferences logins = context.getSharedPreferences("droshed_logins", Context.MODE_PRIVATE);
        String user = logins.getString("droshed_user", null);
        if(user == null) throw new IllegalStateException("User can't be null");
        return user;
    }

    public static String getContentFromFile(File f) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}