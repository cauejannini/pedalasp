package jannini.android.ciclosp.CustomItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class Ciclovia implements Parcelable {
	public String Nome;
	public String Info;
	public Double Dist;
	public ArrayList<LatLng> latLngList = new ArrayList<>();
	public int tipo;
    public LatLngBounds bounds;

	public Ciclovia(String nome, String info, Double dist, ArrayList<LatLng> llList, Integer tipo){
		this.Nome = nome;
		this.Info = info;
		this.Dist = dist;
		this.latLngList = llList;
		this.tipo = tipo;
        getBounds();
	}

	private void getBounds() {

		LatLng origin = latLngList.get(0);
		LatLng destination = latLngList.get(latLngList.size()-1);

		if (origin.latitude < destination.latitude && origin.longitude < destination.longitude) {
            this.bounds = new LatLngBounds(new LatLng(origin.latitude, origin.longitude), new LatLng(destination.latitude, destination.longitude));

		} else if (origin.latitude < destination.latitude && destination.longitude < origin.longitude) {
            this.bounds = new LatLngBounds(new LatLng(origin.latitude, destination.longitude), new LatLng(destination.latitude, origin.longitude));

        } else if (destination.latitude < origin.latitude && origin.longitude < destination.longitude) {
            this.bounds = new LatLngBounds(new LatLng(destination.latitude, origin.longitude), new LatLng(origin.latitude, destination.longitude));

        } else if (destination.latitude < origin.latitude && destination.longitude < origin.longitude) {
            this.bounds = new LatLngBounds(new LatLng(destination.latitude, destination.longitude), new LatLng(origin.latitude, origin.longitude));
		}
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(Nome);
		out.writeString(Info);
		out.writeDouble(Dist);
		out.writeList(latLngList);
		out.writeInt(tipo);
	}

	public static final Parcelable.Creator<Ciclovia> CREATOR = new Parcelable.Creator<Ciclovia>() {
		public Ciclovia createFromParcel(Parcel in) {
			return new Ciclovia(in);
		}

		public Ciclovia[] newArray(int size) {
			return new Ciclovia[size];
		}
	};

	@SuppressWarnings("unchecked")
	public Ciclovia(Parcel in) {
		Nome = in.readString();
		Info = in.readString();
		Dist = in.readDouble();
		latLngList = in.readArrayList(LatLng.class.getClassLoader());
		tipo = in.readInt();

        getBounds();
	}


}
