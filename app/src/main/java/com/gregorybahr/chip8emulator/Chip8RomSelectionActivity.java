package com.gregorybahr.chip8emulator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gregorybahr.chip8emulator.emulators.emulatorbase.Rom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Chip8RomSelectionActivity extends AppCompatActivity {

    private static final String TAG = "Chip8RomSelectionActivity";
    private ListView romListView;
    private List<Rom> romList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rom_selection);

        romList = loadRomList();

        romListView = (ListView) findViewById(R.id.rom_list);
        romListView.setAdapter(new RomListAdapter(this, R.layout.item_rom_list, romList));

        romListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Chip8RomSelectionActivity.this, Chip8EmulatorActivity.class);
                intent.putExtra("rom", romList.get(position));
                startActivity(intent);
            }
        });
    }

    private ArrayList<Rom> loadRomList() {
        ArrayList<Rom> romList = new ArrayList<>();
        try {
            String[] roms = getAssets().list("");
            for(String s : roms) {
                romList.add(new Rom(s, s));
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
