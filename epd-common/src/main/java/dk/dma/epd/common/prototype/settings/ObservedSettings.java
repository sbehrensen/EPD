/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.prototype.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * An abstract base class that can be used when writing classes that maintain a
 * set of application settings in a central place. It allows for clients to
 * register for notifications of changes to any setting maintained by this
 * class.
 * </p>
 * <p>
 * Furthermore, this class provides a {@link ReentrantReadWriteLock} (see
 * instance field {@link #settingLock}) that subclasses can utilize when
 * synchronizing access to the settings values they maintain.
 * </p>
 * 
 * @param <OBSERVER>
 *            The type of the observers observing the {@code ObservedSettings}
 *            for changes.
 * 
 * @author Janus Varmarken
 */
public abstract class ObservedSettings<OBSERVER extends ISettingsObserver> {

    // TODO We may have to do lazy init of this list inside add/remove observer
    // methods if we want to serialize this class.
    /**
     * The list of observers that are to receive notifications when changes
     * occur to this {@code ObservedSettings} instance.
     */
    protected CopyOnWriteArrayList<OBSERVER> observers = new CopyOnWriteArrayList<>();

    /**
     * Provides a read lock and a write lock for accessing and modifying the
     * settings values maintained by this {@link ObservedSettings} instance. The
     * read lock allows for simultaneous read access from many threads while the
     * write lock is exclusive, i.e. only one thread can write at any given time
     * and no threads can read when one thread is writing.
     */
    protected ReentrantReadWriteLock settingLock = new ReentrantReadWriteLock(true);

    /**
     * Add a new observer that is to be notified when any setting is changed. An
     * observer can only be registered once.
     * 
     * @param obs
     *            The observer that is to be notified.
     * @return {@code true} if the observer was successfully added,
     *         {@code false} if the observer was not added (i.e. it was already
     *         registered).
     */
    public boolean addObserver(OBSERVER obs) {
        return this.observers.addIfAbsent(obs);
    }

    /**
     * Removes an observer such that it will no longer be notified when any
     * setting is changed.
     * 
     * @param obs
     *            The observer to remove.
     * @return {@code true} if the observer was successfully removed,
     *         {@code false} if the observer was not registered with this
     *         {@code ObservedSettings}.
     */
    public boolean removeObserver(OBSERVER obs) {
        return this.observers.remove(obs);
    }

    /**
     * Loads settings from a file into memory. If the settings are successfully
     * loaded, {@link #onLoadSuccess(Properties)} is invoked with a
     * {@link Properties} instance containing the loaded settings. Similarly, if
     * an error occurs during load, {@link #onLoadFailure(IOException)} is
     * invoked with an {@link IOException} specifying what went wrong.
     * 
     * @param file
     *            The file containing the settings to be loaded into memory.
     */
    public final void loadFromFile(File file) {
        Properties p = new Properties();
        try (FileReader reader = new FileReader(file)) {
            p.load(reader);
            this.onLoadSuccess(p);
        } catch (IOException e) {
            e.printStackTrace();
            this.onLoadFailure(e);
        }
    }

    /**
     * Persists the settings managed by this instance to a file.
     * 
     * @param file
     *            The file to persist the settings in.
     * @param headerComment
     *            An optional comment at the beginning of the file.
     */
    public final void saveToFile(File file, String headerComment) {
        Properties propsToSave = this.onSaveSettings();
        try (PrintWriter writer = new PrintWriter(file)) {
            propsToSave.store(writer, headerComment);
        } catch (IOException e) {
            e.printStackTrace();
            this.onSaveFailure(e);
        }
    }

    /**
     * This method is invoked when the settings managed by this
     * {@code ObservedSettings} instance is to be persisted in a file (i.e. it
     * is invoked as part of the {@link #saveToFile(File, String)} method).
     * Subclass implementations should return a {@link Properties} instance
     * containing the set of settings that are to be persisted.
     * 
     * @return A {@link Properties} instance containing the set of settings that
     *         are to be persisted in a file.
     */
    protected abstract Properties onSaveSettings();

    /**
     * Invoked when settings have been successfully read from a file. This
     * allows subclasses to perform initialization of variables based on the
     * settings contained in the provided {@link Properties} instance,
     * {@code settings}.
     * 
     * @param settings
     *            Contains the settings that were read from the file.
     */
    protected abstract void onLoadSuccess(Properties settings);

    /**
     * Invoked if an error occurs while reading settings from a file. This
     * allows subclasses to respond to such an error.
     * 
     * @param error
     *            A {@link FileNotFoundException} if the settings file specified
     *            in {@link #loadFromFile(File)} was not found or could not be
     *            read from. An {@link IOException} if an error occurred while
     *            reading the settings file.
     */
    protected abstract void onLoadFailure(IOException error);

    /**
     * Invoked if an error occurs while saving settings to a file. This allows
     * subclasses to respond to such an error.
     * 
     * @param error
     *            A {@link FileNotFoundException} if the settings file specified
     *            in {@link #saveToFile(File, String)} was not found or could
     *            not be written to. An {@link IOException} if an error occurred
     *            while persisting the settings to the file.
     */
    protected abstract void onSaveFailure(IOException error);
}
