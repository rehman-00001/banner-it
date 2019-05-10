package com.codemonk_labs.bannerit.fonts;

import com.codemonk_labs.bannerit.R;

public class Fonts {

    public static int of(int fontIndex) {
        return list[fontIndex].resourceId;
    }

    public static class FontDetail {
        String name;
        int resourceId;

        FontDetail(String name, int resourceId) {
            this.name = name;
            this.resourceId = resourceId;
        }
    }

    public static final FontDetail[] list = new FontDetail[] {
            new FontDetail("Roboto", R.font.roboto),
            new FontDetail("Aclonica", R.font.aclonica),
            new FontDetail("Acme", R.font.acme),
            new FontDetail("Akronim", R.font.akronim),
            new FontDetail("Allan", R.font.allerta),
            new FontDetail("Allerta", R.font.allerta),
            new FontDetail("Amiko", R.font.amiko),
            new FontDetail("BadScript", R.font.bad_script),
            new FontDetail("BANGERS", R.font.bangers),
            new FontDetail("Berkshire swash", R.font.berkshire_swash),
            new FontDetail("Bubblegum Sans", R.font.bubblegum_sans),
            new FontDetail("Butcherman", R.font.butcherman),
            new FontDetail("Butterfly kids", R.font.butterfly_kids),
            new FontDetail("Cedarville cursive", R.font.cedarville_cursive),
            new FontDetail("Chango", R.font.chango),
            new FontDetail("Chelsea market", R.font.chelsea_market),
            new FontDetail("Cherry cream soda", R.font.cherry_cream_soda),
            new FontDetail("Coming soon", R.font.coming_soon),
            new FontDetail("Cutive", R.font.cutive),
            new FontDetail("Montserrat", R.font.montserrat)
    };

}
