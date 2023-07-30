package random.gba.loader;

import fedata.gba.fe6.FE6Data;
import fedata.gba.fe7.FE7Data;
import fedata.gba.fe8.FE8Data;
import fedata.general.FEBase;
import io.FileHandler;
import util.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapSpriteManager {

    private class SpriteChange {
        protected String key;
        protected byte[] value;
        protected Integer classId;
        protected Integer replacedClass;

        public SpriteChange(String key, byte[] value, Integer classId, Integer replacedClass) {
            this.key = key;
            this.value = value;
            this.classId = classId;
            this.replacedClass = replacedClass;
        }
    }

    private List<SpriteChange> changes = new ArrayList<>();
    private FEBase.GameType type;
    private long tableOffset;
    private int numberEntriesVanilla;
    private int bytesPerEntry;
    private byte[] table;

    private boolean repoint;

    public MapSpriteManager(FEBase.GameType type, FileHandler handler) {
        this.type = type;
        this.repoint = !FEBase.GameType.FE8.equals(type);
        setTableOffset(handler);
        setBytesPerEntry();
        setNumberEntriesVanilla();
        table = handler.readBytesAtOffset(tableOffset, bytesPerEntry * numberEntriesVanilla);
    }

    public void compileDiffs(DiffCompiler compiler, FreeSpaceManager freeSpace) {
        if (changes.isEmpty()) {
            // no sprites added, do nothing.
            return;
        }

        if (repoint) {
            // first write the vanilla table to a new place in freespace
            long newSpriteTableOffset = freeSpace.setValue(table, "Repointed Sprite Table", true);

            // then append all the values that should be added
            for (SpriteChange addedSprite : changes) {
                freeSpace.setValue(addedSprite.value, addedSprite.key);
            }

            // repoint the table so that the new values are used
            compiler.findAndReplace(new FindAndReplace(WhyDoesJavaNotHaveThese.bytesFromAddress(tableOffset), WhyDoesJavaNotHaveThese.bytesFromAddress(newSpriteTableOffset), true));
        } else {
            // If we don't have to repoint the table, replace the specified class
            for (SpriteChange change : changes) {
                if (change.replacedClass == null) {
                    throw new IllegalArgumentException("Replaced class is missing");
                }
                compiler.addDiff(new Diff(tableOffset + ((change.replacedClass - 1) * bytesPerEntry), bytesPerEntry, change.value, null));
            }
        }
    }

    public void addSprite(String key, byte[] value, Integer classId, Integer replacedClass) {
        changes.add(new SpriteChange(key, value, classId, replacedClass));
    }

    public void duplicateSprite(String key, int classToCopy) {
        addSprite(key, spriteForClass(classToCopy), classToCopy, null);
    }

    public void duplicateSprite(String key, int classToCopy, int classToReplace) {
        addSprite(key, spriteForClass(classToCopy), classToCopy, classToReplace);
    }

    private byte[] spriteForClass(int classId) {
        return WhyDoesJavaNotHaveThese.subArray(table, (classId - 1) * bytesPerEntry, bytesPerEntry);
    }

    private void setTableOffset(FileHandler handler) {
        long pointer;
        switch (type) {
            case FE6:
                pointer =  FE6Data.ClassMapSpriteTablePointer;
                break;
            case FE7:
                pointer = FE7Data.ClassMapSpriteTablePointer;
                break;
            case FE8:
                pointer = FE8Data.ClassMapSpriteTablePointer;
                break;
            default:
                throw new IllegalArgumentException("Unknown Game");
        }
        tableOffset = FileReadHelper.readAddress(handler, pointer);
    }

    private void setBytesPerEntry() {
        switch (type) {
            case FE6:
                bytesPerEntry = FE6Data.BytesPerMapSpriteTableEntry;
                break;
            case FE7:
                bytesPerEntry = FE7Data.BytesPerMapSpriteTableEntry;
                break;
            case FE8:
                bytesPerEntry = FE8Data.BytesPerMapSpriteTableEntry;
                break;
            default:
        }
    }

    private void setNumberEntriesVanilla() {
        switch (type) {
            case FE6:
                numberEntriesVanilla = FE6Data.NumberOfMapSpriteEntries;
                break;
            case FE7:
                numberEntriesVanilla = FE7Data.NumberOfMapSpriteEntries;
                break;
            case FE8:
                numberEntriesVanilla = FE8Data.NumberOfMapSpriteEntries;
                break;
            default:
        }
    }
}
