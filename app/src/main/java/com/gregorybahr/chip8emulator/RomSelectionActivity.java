package com.gregorybahr.chip8emulator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gregorybahr.chip8emulator.emulator.Rom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RomSelectionActivity extends AppCompatActivity {

    private static final String TAG = RomSelectionActivity.class.getSimpleName();
    private ListView romListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rom_selection);

        romListView = (ListView) findViewById(R.id.rom_list);
        romListView.setAdapter(new RomListAdapter(this, R.layout.item_rom_list, loadRomList()));
    }

    private ArrayList<Rom> loadRomList() {
        ArrayList<Rom> romList = new ArrayList<>();
        try {
            String[] roms = getAssets().list("roms");
            for(String s : roms) {
                romList.add(new Rom("roms/"+s, s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return romList;
    }

    private class RomListAdapter extends ArrayAdapter<Rom> {
        private List<Rom> objects;
        private int resource;

        public RomListAdapter(Context context, int resource, List<Rom> objects) {
            super(context, resource, objects);
            this.objects = objects;
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(resource, parent, false);
            }
            Rom rom = objects.get(position);

            TextView name = (TextView) convertView.findViewById(R.id.rom_name);
            name.setText(rom.getName());

            return convertView;
        }
    }
}
