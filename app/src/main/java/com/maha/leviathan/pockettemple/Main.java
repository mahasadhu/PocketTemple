package com.maha.leviathan.pockettemple;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.maha.leviathan.pockettemple.usermanagement.Account;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

/**
 * Created by Leviathan on 4/23/2015.
 */
public class Main extends MaterialNavigationDrawer {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void init(Bundle bundle) {

        sharedPreferences = getSharedPreferences(getString(R.string.sharedprefname), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        enableToolbarElevation();
        disableLearningPattern();

        setDrawerHeaderImage(R.drawable.pura);
        setUsername("Pocket Temple");
        setUserEmail(sharedPreferences.getString("nama", "Find Any Temple !"));

        this.addSection(newSection("Maps", R.mipmap.ic_action_map, new MapFrag()));
        this.addSection(newSection("List Pura", R.mipmap.ic_action_list_2, new ListPura()));

        if (sharedPreferences.getBoolean("logged", false) == true){
            this.addDivisor();
            this.addSection(newSection("Add New Pura", R.mipmap.ic_action_add, new Intent(this, AddPuras.class)));
            this.addSection(newSection("Your Added Pura", R.mipmap.ic_action_document, new MyListPura()));
        }

        this.addBottomSection(newSection("Account", R.mipmap.ic_action_user, new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {
                Intent a = new Intent(Main.this, Account.class);
                startActivity(a);
                finish();
            }
        }));
//        this.addBottomSection(newSection("Settings", R.mipmap.ic_action_gear, new Intent(this, Setting.class)));


    }
}
