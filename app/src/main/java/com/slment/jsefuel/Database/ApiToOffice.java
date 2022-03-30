package com.slment.jsefuel.Database;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiToOffice {

    @FormUrlEncoded
    @POST("createFuelFill")
    Call<ResponseBody> createFuelFill(
        @Field("fuelId") int fuelId,
        @Field("vehicleId") int vehicleId,
        @Field("employeeId") int employeeId,
        @Field("fillDate") String fillDate,
        @Field("miles") String miles,
        @Field("odometer") String odometer,
        @Field("quantity") String quantity,
        @Field("fillCost") String fillCost,
        @Field("providerId") int providerId,
        @Field("lastUpdated") String lastUpdated,
        @Field("lastUpdatedBy") int lastUpdatedBy
    );

    @FormUrlEncoded
    @POST("createOilChange")
    Call<ResponseBody> createOilChange(
            @Field("oilChangeId") int oilChangeId,
            @Field("vehicleId") int vehicleId,
            @Field("employeeId") int employeeId,
            @Field("oilChangeDate") String oilChangeDate,
            @Field("odometer") String odometer,
            @Field("totalCost") String totalCost,
            @Field("providerId") int providerId,
            @Field("lastUpdated") String lastUpdated,
            @Field("lastUpdatedBy") int lastUpdatedBy
    );
}
