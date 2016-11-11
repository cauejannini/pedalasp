package jannini.android.ciclosp.CustomItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Bicicletario implements Parcelable{
	public String Nome;
	public Double Lat;
	public Double Lng;
	public String address;
	public String Tipo;
	public int Vagas;
	public String Emprestimo;
	public String Horario;
	public LatLng Localizacao;
        
	public Bicicletario(String nom, Double lat, Double lng, String address, String t, int v, String emp, String hor){
		this.Nome = nom;
		this.Lat = lat;
		this.Lng = lng;
		this.address = address;
		this.Tipo = t;
		this.Vagas = v;
		this.Emprestimo= emp;
		this.Horario= hor;
	}

	public Bicicletario(Parcel in) {
		Nome = in.readString();
		Lat = in.readDouble();
		Lng = in.readDouble();
		address = in.readString();
		Tipo= in.readString();
		Vagas = in.readInt();
		Emprestimo = in.readString();
		Horario = in.readString();
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(Nome);
		out.writeDouble(Lat);
		out.writeDouble(Lng);
		out.writeString(address);
		out.writeString(Tipo);
		out.writeInt(Vagas);
		out.writeString(Emprestimo);
		out.writeString(Horario);
	}

	public boolean hasEmprestimo() {
		return this.Emprestimo.equals("s");
	}
	public LatLng getLatLng() {
		Localizacao = new LatLng(Lat, Lng);
		return Localizacao;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Bicicletario> CREATOR = new Parcelable.Creator<Bicicletario>() {
		public Bicicletario createFromParcel(Parcel in) {
			return new Bicicletario(in);
		}

		public Bicicletario[] newArray(int size) {
			return new Bicicletario[size];
		}
	};


}
