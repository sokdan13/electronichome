package com.example.electronichome.domain.usecase

import com.example.electronichome.data.repository.AnnouncementRepository
import com.example.electronichome.data.repository.ApartmentRepository
import com.example.electronichome.data.repository.GuestPassRepository
import com.example.electronichome.data.repository.MeterRepository
import com.example.electronichome.domain.model.*
import com.example.electronichome.domain.usecase.announcement.GetAnnouncementsUseCase
import com.example.electronichome.domain.usecase.apartment.AddApartmentUseCase
import com.example.electronichome.domain.usecase.guestpass.CreateGuestPassUseCase
import com.example.electronichome.domain.usecase.meter.SubmitMeterReadingUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AllUseCasesTest {

    // Репозитории
    private lateinit var apartmentRepository: ApartmentRepository
    private lateinit var meterRepository: MeterRepository
    private lateinit var guestPassRepository: GuestPassRepository
    private lateinit var announcementRepository: AnnouncementRepository

    // UseCase
    private lateinit var addApartmentUseCase: AddApartmentUseCase
    private lateinit var submitMeterReadingUseCase: SubmitMeterReadingUseCase
    private lateinit var createGuestPassUseCase: CreateGuestPassUseCase
    private lateinit var getAnnouncementsUseCase: GetAnnouncementsUseCase

    @Before
    fun setUp() {
        apartmentRepository   = mockk()
        meterRepository       = mockk()
        guestPassRepository   = mockk()
        announcementRepository = mockk()

        addApartmentUseCase      = AddApartmentUseCase(apartmentRepository)
        submitMeterReadingUseCase = SubmitMeterReadingUseCase(meterRepository)
        createGuestPassUseCase   = CreateGuestPassUseCase(guestPassRepository)
        getAnnouncementsUseCase  = GetAnnouncementsUseCase(announcementRepository)
    }

    // ─── AddApartmentUseCase ───────────────────────────────────────────────────

    @Test
    fun `(Квартира) пустой город возвращает ошибку`() = runTest {
        val result = addApartmentUseCase(
            ApartmentRequest(city = "", street = "ул. Ленина",
                house = "1", apartment = "10", floor = 3)
        )
        assertTrue(result.isFailure)
        assertEquals("Укажите город", result.exceptionOrNull()?.message)
    }

    @Test
    fun `(Квартира) пустая улица возвращает ошибку`() = runTest {
        val result = addApartmentUseCase(
            ApartmentRequest(city = "Москва", street = "",
                house = "1", apartment = "10", floor = 3)
        )
        assertTrue(result.isFailure)
        assertEquals("Укажите улицу", result.exceptionOrNull()?.message)
    }



    @Test
    fun `(Квартира) пустой дом возвращает ошибку`() = runTest {
        val result = addApartmentUseCase(
            ApartmentRequest(city = "Москва", street = "ул. Ленина",
                house = "", apartment = "10", floor = 3)
        )
        assertTrue(result.isFailure)
        assertEquals("Укажите номер дома", result.exceptionOrNull()?.message)
    }

    @Test
    fun `(Квартира) некорректный этаж возвращает ошибку`() = runTest {
        val result = addApartmentUseCase(
            ApartmentRequest(city = "Москва", street = "ул. Ленина",
                house = "1", apartment = "10", floor = 0)
        )
        assertTrue(result.isFailure)
        assertEquals("Некорректный этаж", result.exceptionOrNull()?.message)
    }

    @Test
    fun `(Квартира) корректные данные возвращают успех`() = runTest {
        val request = ApartmentRequest(city = "Москва", street = "ул. Ленина",
            house = "5", apartment = "42", floor = 3)
        val expected = ApartmentResponse(
            id = "uuid-1", userId = "user-1", city = "Москва",
            street = "ул. Ленина", house = "5", apartment = "42",
            floor = 3, status = "PENDING", accountNumber = null,
            rejectionNote = null, createdAt = "2024-01-01", updatedAt = "2024-01-01"
        )
        coEvery { apartmentRepository.addApartment(request) } returns Result.success(expected)

        val result = addApartmentUseCase(request)
        assertTrue(result.isSuccess)
        assertEquals("uuid-1", result.getOrNull()?.id)
    }

    // ─── SubmitMeterReadingUseCase ─────────────────────────────────────────────

    @Test
    fun `(Счётчики) некорректный месяц возвращает ошибку`() = runTest {
        val result = submitMeterReadingUseCase(
            MeterReadingRequest(apartmentId = "apt-1", month = 13,
                year = 2024, hotWater = 100.0)
        )
        assertTrue(result.isFailure)
        assertEquals("Некорректный месяц", result.exceptionOrNull()?.message)
    }

    @Test
    fun `(Счётчики) некорректный год возвращает ошибку`() = runTest {
        val result = submitMeterReadingUseCase(
            MeterReadingRequest(apartmentId = "apt-1", month = 5,
                year = 1999, hotWater = 100.0)
        )
        assertTrue(result.isFailure)
        assertEquals("Некорректный год", result.exceptionOrNull()?.message)
    }

    @Test
    fun `(Счётчики) все показания пустые возвращают ошибку`() = runTest {
        val result = submitMeterReadingUseCase(
            MeterReadingRequest(apartmentId = "apt-1", month = 5, year = 2024)
        )
        assertTrue(result.isFailure)
        assertEquals("Введите хотя бы одно показание", result.exceptionOrNull()?.message)
    }

    @Test
    fun `(Счётчики) корректные данные возвращают успех`() = runTest {
        val request  = MeterReadingRequest(apartmentId = "apt-1",
            month = 5, year = 2024, hotWater = 123.456, coldWater = 98.0)
        val expected = MeterReadingResponse(
            id = "meter-1", apartmentId = "apt-1", month = 5, year = 2024,
            hotWater = 123.456, coldWater = 98.0,
            createdAt = "2024-05-01", updatedAt = "2024-05-01"
        )
        coEvery { meterRepository.submitReading(request) } returns Result.success(expected)

        val result = submitMeterReadingUseCase(request)
        assertTrue(result.isSuccess)
        assertEquals(123.456, result.getOrNull()?.hotWater)
    }

    // ─── CreateGuestPassUseCase ────────────────────────────────────────────────

    @Test
    fun `(Пропуск) пустой apartmentId возвращает ошибку`() = runTest {
        val result = createGuestPassUseCase(
            GuestPassCreateDto(apartmentId = "", durationMinutes = 30)
        )
        assertTrue(result.isFailure)
        assertEquals("Квартира не выбрана", result.exceptionOrNull()?.message)
    }

    @Test
    fun `(Пропуск) недопустимый срок действия возвращает ошибку`() = runTest {
        val result = createGuestPassUseCase(
            GuestPassCreateDto(apartmentId = "apt-1", durationMinutes = 15)
        )
        assertTrue(result.isFailure)
        assertEquals("Недопустимый срок действия", result.exceptionOrNull()?.message)
    }

    @Test
    fun `(Пропуск) корректные данные возвращают пропуск`() = runTest {
        val dto      = GuestPassCreateDto(apartmentId = "apt-1", durationMinutes = 30)
        val expected = GuestPassResponse(
            id = "pass-1", apartmentId = "apt-1", token = "abc123token",
            expiresAt = "2024-05-01T12:30:00Z", createdAt = "2024-05-01T12:00:00Z",
            isValid = true, minutesLeft = 29
        )
        coEvery { guestPassRepository.createPass(dto) } returns Result.success(expected)

        val result = createGuestPassUseCase(dto)
        assertTrue(result.isSuccess)
        assertEquals("abc123token", result.getOrNull()?.token)
    }

    // ─── GetAnnouncementsUseCase ───────────────────────────────────────────────

    @Test
    fun `(Объявления) без фильтра возвращает все объявления`() = runTest {
        val list = listOf(
            AnnouncementResponse("1", "Заголовок 1", "Описание 1",
                "IMPORTANT", "Важно", null, "2024-05-01", "2024-05-01"),
            AnnouncementResponse("2", "Заголовок 2", "Описание 2",
                "NEWS", "Новости", null, "2024-05-02", "2024-05-02")
        )
        coEvery { announcementRepository.getAnnouncements(null) } returns Result.success(list)

        val result = getAnnouncementsUseCase(null)
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `(Объявления) с фильтром IMPORTANT возвращает одно объявление`() = runTest {
        val list = listOf(
            AnnouncementResponse("1", "Заголовок 1", "Описание 1",
                "IMPORTANT", "Важно", null, "2024-05-01", "2024-05-01")
        )
        coEvery { announcementRepository.getAnnouncements("IMPORTANT") } returns Result.success(list)

        val result = getAnnouncementsUseCase("IMPORTANT")
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("IMPORTANT", result.getOrNull()?.first()?.category)
    }

    @Test
    fun `(Объявления) ошибка репозитория возвращает failure`() = runTest {
        coEvery { announcementRepository.getAnnouncements(any()) } returns
                Result.failure(Exception("Нет соединения"))

        val result = getAnnouncementsUseCase(null)
        assertTrue(result.isFailure)
        assertEquals("Нет соединения", result.exceptionOrNull()?.message)
    }
}