package jannini.android.ciclosp.CustomItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Estacao implements Parcelable {
	public int Numero;
	public String Nome;
	public String Descricao;
	public Double lat;
	public Double lng;
	public String status1;
	public String status2;
	public int bikes;
	public int tamanho;

	public Estacao(int numero, String s1, String s2, Double lat, Double lng){
		this.Numero = numero;
		this.Nome = s1;
		this.Descricao = s2;
		this.lat = lat;
		this.lng = lng;
		this.status1 = null;
		this.status2 = null;
		this.bikes = 999;
		this.tamanho = 999;
	}

	public Estacao(int numero, String s1, String s2, Double lat, Double lng, String status1, String status2, int bikes, int tamanho){
		this.Numero = numero;
		this.Nome = s1;
		this.Descricao = s2;
		this.lat = lat;
		this.lng = lng;
		this.status1 = status1;
		this.status2 = status2;
		this.bikes = bikes;
		this.tamanho = tamanho;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(Numero);
		out.writeString(Nome);
		out.writeString(Descricao);
		out.writeDouble(lat);
		out.writeDouble(lng);
		out.writeString(status1);
		out.writeString(status2);
		out.writeInt(bikes);
		out.writeInt(tamanho);
	}

	public static final Parcelable.Creator<Estacao> CREATOR = new Parcelable.Creator<Estacao>() {
		public Estacao createFromParcel(Parcel in) {
			return new Estacao(in);
		}

		public Estacao[] newArray(int size) {
			return new Estacao[size];
		}
	};


	public Estacao(Parcel in) {
		Numero = in.readInt();
		Nome = in.readString();
		Descricao = in.readString();
		lat = in.readDouble();
		lng = in.readDouble();
		status1 = in.readString();
		status2 = in.readString();
		bikes = in.readInt();
		tamanho = in.readInt();

	}

	public LatLng getLatLng() {
		return new LatLng(lat, lng);
	}
}
