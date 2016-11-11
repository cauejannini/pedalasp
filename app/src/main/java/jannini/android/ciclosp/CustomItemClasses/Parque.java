package jannini.android.ciclosp.CustomItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Parque implements Parcelable{
	public String Nome;
	public String Endereco;
	public Double Lat;
	public Double Lng;
	public String Descricao;
	public String Funcionamento;
	public String Ciclovia;
	public String Contato;
	public int Wifi;
	public LatLng Localizacao;
        
	public Parque(String s1, String s2, Double lat, Double lng, String desc, String func, String ciclo, String contato, int i){
		this.Nome = s1;
		this.Endereco = s2;
		this.Lat = lat;
		this.Lng = lng;
		this.Descricao = desc;
		this.Funcionamento = func;
		this.Ciclovia = ciclo;
		this.Contato = contato;
		this.Wifi = i;
	}

	public Parque(Parcel in) {
		Nome = in.readString();
		Endereco= in.readString();
		Lat = in.readDouble();
		Lng = in.readDouble();
		Descricao = in.readString();
		Funcionamento = in.readString();
		Ciclovia = in.readString();
		Contato = in.readString();
		Wifi = in.readInt();
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(Nome);
		out.writeString(Endereco);
		out.writeDouble(Lat);
		out.writeDouble(Lng);
		out.writeString(Descricao);
		out.writeString(Funcionamento);
		out.writeString(Ciclovia);
		out.writeString(Contato);
		out.writeInt(Wifi);
	}

	public LatLng getLatLng() {
		Localizacao = new LatLng(Lat, Lng);
		return Localizacao;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Parque> CREATOR = new Parcelable.Creator<Parque>() {
		public Parque createFromParcel(Parcel in) {
			return new Parque(in);
		}

		public Parque[] newArray(int size) {
			return new Parque[size];
		}
	};


}
