package com.gregorybahr.chip8emulator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gregorybahr.chip8emulator.emulators.EmulatorType;
import com.gregorybahr.chip8emulator.emulators.emulatorbase.Rom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RomListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RomListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RomListFragment extends Fragment {

    private static final String ARG_EMULATOR = "emulatorType";

    private EmulatorType emulatorType;
    private ListView romListView;
    private List<Rom> romList;

    private OnFragmentInteractionListener mListener;

    public RomListFragment() {
    }

    public static RomListFragment newInstance(EmulatorType emulatorType) {
        RomListFragment fragment = new RomListFragment();
        Bundle args = new Bundle();
        args.putSerializable("emulatorType", emulatorType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            emulatorType = (EmulatorType) getArguments().getSerializable(ARG_EMULATOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rom_selection, container, false);
        romList = loadRomList();

        romListView = (ListView) v.findViewById(R.id.rom_list);
        romListView.setAdapter(new RomListAdapter(getContext(), R.layout.item_rom_list, romList));
        romListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onRomSelected(romList.get(position));
            }
        });
        return v;
    }

    private ArrayList<Rom> loadRomList() {
        String folder = "";
        switch (emulatorType) {
            case CHIP8:
                folder = "chip8";
                break;
            case NES:
                folder = "nes";
                break;
            default:
                Log.wtf("RomListFragment", "Invalid EmulatorType Type");
        }
        ArrayList<Rom> romList = new ArrayList<>();
        try {
            String[] roms = getActivity().getAssets().list(folder);
            for(String s : roms) {
                romList.add(new Rom(folder+"/"+s, s, emulatorType));
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
                convertView = getActivity().getLayoutInflater().inflate(resource, parent, false);
            }
            Rom rom = objects.get(position);

            TextView name = (TextView) convertView.findViewById(R.id.rom_name);
            name.setText(rom.getName());

            return convertView;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onRomSelected(Rom rom);
    }
}
