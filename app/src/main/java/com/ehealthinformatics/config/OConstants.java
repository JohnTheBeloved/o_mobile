/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 18/12/14 11:28 AM
 */
package com.ehealthinformatics.config;

public class OConstants {
    public static final String URL_ODOO = "http://192.168.8.101:8069,http://192.168.8.100:8069,http://192.168.8.102:8069,http://192.168.8.103:8069";
    public static final String URL_ODOO_RESET_PASSWORD = URL_ODOO + "/web/reset_password";
    public static final String URL_ODOO_SIGN_UP = URL_ODOO + "/web/signup";
    public static final String URL_ODOO_MOBILE_GIT_HUB = "https://github.com/JohnTheBeloved/o_mobile";
    public static final String URL_ODOO_APPS_ON_PLAY_STORE = "https://play.google.com/store/apps/developer?id=Odoo+SA";


    public static final int RPC_REQUEST_TIME_OUT = 30000; // 30 Seconds
    public static final int RPC_REQUEST_RETRIES = 3; // Retries when timeout
    public static String CURRENCY_SYMBOL = " ";

    /**
     * Database version. Required to
     * change in increment order
     * when you change your database model in case of released apk.
     *
     * When dealing with DATABASE_VERSION, you need to override onModelUpgrade() method
     * in each of the modelName class for applying upgrade script for that model.
     */
    public static final int DATABASE_VERSION = 1;

    public static Integer DATA_SYNC_LIMIT = 9000;
    public static Integer DATA_SYNC_DEPTH = 8;

    public static int getSyncLimit(String modelName){
        return DATA_SYNC_LIMIT;
    }

    public static int getSyncDepth(String modelName){
        return DATA_SYNC_DEPTH;
    }

    public static boolean alwaysSyncRelations(String modelName) {
        if(modelName.equalsIgnoreCase("pos.session"))
            return true;
        else return false;
    }

    public static int getAppCompany() {
        return 3;
    }
}
