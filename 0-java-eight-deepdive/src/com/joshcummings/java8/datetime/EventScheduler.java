package com.joshcummings.java8.datetime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Stream;

public class EventScheduler {
	private static class PeriodicEvent {
		private final LocalDate startDate;
		private final Period period;
		private final TemporalAdjuster ta;
		
		public PeriodicEvent(LocalDate startDate, Period period,
				TemporalAdjuster ta) {
			this.startDate = startDate.with(ta);
			this.period = period;
			this.ta = ta;
		}

		public Stream<LocalDate> dates() {
			return Stream.iterate(startDate, (date) ->
					date.plus(period).with(ta));
		}
		
	}
	
	public static void main(String[] args) {
		PeriodicEvent event = new PeriodicEvent(
				LocalDate.now(),
				Period.ofMonths(1),
				TemporalAdjusters.firstDayOfMonth()
				);
		
		event.dates().limit(5).forEach(System.out::println);
		
		PeriodicEvent everySecondWednesday = new PeriodicEvent(
				LocalDate.now(),
				Period.ofMonths(1),
				TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.WEDNESDAY)
				);
		everySecondWednesday.dates().limit(5).forEach(System.out::println);
		
		// the 15th of every month
		PeriodicEvent fifteenthOfEveryMonth = new PeriodicEvent(
				LocalDate.of(2015, Month.JUNE, 15),
				Period.ofMonths(1),
				TemporalAdjusters.ofDateAdjuster((date) -> date)
				);
		fifteenthOfEveryMonth.dates().limit(5).forEach(System.out::println);
	}
}
