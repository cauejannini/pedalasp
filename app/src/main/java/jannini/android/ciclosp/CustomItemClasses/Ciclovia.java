package jannini.android.ciclosp.CustomItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Ciclovia implements Parcelable {
	public String Nome;
	public String Info;
	public Double Dist;
	public ArrayList<LatLng> latLngList = new ArrayList<>();
	public int tipo;

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

	public Ciclovia(String nome, String info, Double dist, ArrayList<LatLng> llList, Integer tipo){
		this.Nome = nome;
		this.Info = info;
		this.Dist = dist;
		this.latLngList = llList;
		this.tipo = tipo;
	}

	@SuppressWarnings("unchecked")
	public Ciclovia(Parcel in) {
		Nome = in.readString();
		Info = in.readString();
		Dist = in.readDouble();
		latLngList = in.readArrayList(LatLng.class.getClassLoader());
		tipo = in.readInt();
	}


}
