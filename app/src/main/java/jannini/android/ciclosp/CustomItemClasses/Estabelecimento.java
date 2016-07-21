package jannini.android.ciclosp.CustomItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import jannini.android.ciclosp.R;

/**
 * Created by cauejannini on 04/07/16.
 */
public class Estabelecimento implements Parcelable {

    public Double lat;
    public Double lng;
    public String name, address, opHours, shortDesc, tel, other, timestamp;
    public int store, newBikes, usedBikes, accessories, workshop, shower, coffee, verified;

    public Estabelecimento (String name, String address, Double lat, Double lng, String tel, String opHours, String shortDesc, int store, int newBikes, int usedBikes, int accessories, int workshop, int shower, int coffee, String other, int verified, String timestamp) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.tel = tel;
        this.opHours = opHours;
        this.shortDesc = shortDesc;
        this.store = store;
        this.newBikes = newBikes;
        this.usedBikes = usedBikes;
        this.accessories = accessories;
        this.workshop = workshop;
        this.shower = shower;
        this.coffee = coffee;
        this.other = other;
        this.verified = verified;
        this.timestamp= timestamp;
    }

    public Estabelecimento (String name, String address, Double lat, Double lng, String tel, String opHours, String shortDesc, int store, int workshop, int shower, int coffee, String other, int verified, String timestamp) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.tel = tel;
        this.opHours = opHours;
        this.shortDesc = shortDesc;
        this.store = store;
        this.workshop = workshop;
        this.shower = shower;
        this.coffee = coffee;
        this.other = other;
        this.verified = verified;
        this.timestamp= timestamp;
    }

    public LatLng getLatLng() {
        return new LatLng(this.lat, this.lng);
    }

    public int getIcon() {

        if (this.verified == 0) {
            if (this.store == 1) {
                if (this.workshop == 1) {
                    // Store + Workshop icon
                }
                if (this.shower == 1) {
                    // Store + shower
                }
                if (this.coffee == 1) {
                    // Store + coffee
                }
                // Store icon
            } else if (this.workshop == 1) {
                if (this.shower == 1) {
                    // Workshop + shower
                }
                if (this.coffee == 1) {
                    // Workshop + coffee
                }
                // Workshop icon
            } else if (this.shower == 1) {
                if (this.coffee == 1) {
                    // Shower + coffee
                }
                //Shower icon
            }
        }

        return R.drawable.mapic_estabelecimento;
    }

    public Estabelecimento(Parcel in) {
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        tel = in.readString();
        opHours = in.readString();
        shortDesc = in.readString();
        store = in.readInt();
        newBikes = in.readInt();
        usedBikes = in.readInt();
        accessories = in.readInt();
        workshop = in.readInt();
        shower = in.readInt();
        coffee = in.readInt();
        other = in.readString();
        verified = in.readInt();
        timestamp = in.readString();
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(address);
        out.writeDouble(lat);
        out.writeDouble(lng);
        out.writeString(tel);
        out.writeString(opHours);
        out.writeString(shortDesc);
        out.writeInt(store);
        out.writeInt(newBikes);
        out.writeInt(usedBikes);
        out.writeInt(accessories);
        out.writeInt(workshop);
        out.writeInt(shower);
        out.writeInt(coffee);
        out.writeString(other);
        out.writeInt(verified);
        out.writeString(timestamp);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public static final Parcelable.Creator<Estabelecimento> CREATOR = new Parcelable.Creator<Estabelecimento>() {
        public Estabelecimento createFromParcel(Parcel in) {
            return new Estabelecimento(in);
        }

        public Estabelecimento[] newArray(int size) {
            return new Estabelecimento[size];
        }
    };



}
