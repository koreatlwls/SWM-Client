package org.retriever.dailypet.data.network.main

import okhttp3.ResponseBody
import org.retriever.dailypet.model.main.CareInfo
import org.retriever.dailypet.model.main.CareList
import org.retriever.dailypet.model.main.PetDaysResponse
import org.retriever.dailypet.model.signup.pet.PetList
import retrofit2.Response
import retrofit2.http.*

interface HomeApiService {
    @GET("api/v1/main/pets/{petId}/days")
    suspend fun getDay(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<PetDaysResponse>

    @GET("api/v1/pets/{petId}/cares")
    suspend fun getCareList(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<CareList>

    @GET("api/v1/families/{familyId}/pets")
    suspend fun getPetList(
        @Path("familyId") familyId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
    ): Response<PetList>

    @POST("api/v1/pets/{petId}/care")
    suspend fun postPetCare(
        @Path("petId") petId: Int,
        @Header("X-AUTH-TOKEN") jwt: String,
        @Body careInfo: CareInfo
    ): Response<ResponseBody>

}