package br.com.viavarejo.viamais.teste.performance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.BiFunction;

import reactor.core.publisher.Mono;

class TestePerfomandeComString {
	public static void main(String[] args) {
		final TestePerfomandeComString t = new TestePerfomandeComString();
		final int from = 1, to = 800000;
		t.calcularTempo(from, to, t.somar);
		// t.calcularTempo(from, to, t.somarParallel2Pices);
		// t.calcularTempo(from, to, t.somarParallel4Pices);
		// t.calcularTempo(from, to, t.somarParallel6Pices);
	}

	public BiFunction<Integer, Integer, Mono<Long>> monoBuilder = (init, end) -> {
		return Mono.just(Long.valueOf(1)).map(initValue -> {
			String x = "";
			final int rate = end / 10;
			BigDecimal tot = BigDecimal.ZERO;
			long count = initValue;
			for (int i = init; i <= end; i++) {
				tot = tot.add(BigDecimal.valueOf(i));
				if (count >= rate) {
					count = 1;
					x = "";
				}
				else {
					x += "X";
					count++;
				}
			}
			return count;
		});
	};

	public BiFunction<Integer, Integer, Mono<Long>> somar = (init, end) -> monoBuilder.apply(init, end);

	public BiFunction<Integer, Integer, Mono<Long>> somarParallel2Pices = (init, end) -> {
		final int rate = end / 2;
		int from = init, to = rate;
		final var first = monoBuilder.apply(from, to);

		from = to + 1;
		to = end;
		final var second = monoBuilder.apply(from, to);
		return Mono.zip(first, second).flatMap(tuple -> Mono.just(tuple.getT1() + tuple.getT2()));
	};

	public BiFunction<Integer, Integer, Mono<Long>> somarParallel4Pices = (init, end) -> {
		final int rate = end / 4;
		int from = init, to = rate;
		final var first = monoBuilder.apply(from, to);

		from = to + 1;
		to = 2 * rate;
		final var second = monoBuilder.apply(from, to);

		from = to + 1;
		to = 3 * rate;
		final var third = monoBuilder.apply(from, to);

		from = to + 1;
		to = 4 * rate;
		if (to < end) {
			to = end;
		}
		final var fouth = monoBuilder.apply(from, to);
		return Mono.zip(first, second, third, fouth).flatMap(tuple -> Mono.just(tuple.getT1() + tuple.getT2() + tuple.getT3() + tuple.getT4()));
	};

	public BiFunction<Integer, Integer, Mono<Long>> somarParallel6Pices = (init, end) -> {
		final int rate = end / 6;
		int from = init, to = rate;
		final var first = monoBuilder.apply(from, to);

		from = to + 1;
		to = 2 * rate;
		final var second = monoBuilder.apply(from, to);

		from = to + 1;
		to = 3 * rate;
		final var third = monoBuilder.apply(from, to);

		from = to + 1;
		to = 4 * rate;
		final var fourth = monoBuilder.apply(from, to);

		from = to + 1;
		to = 5 * rate;
		final var fifth = monoBuilder.apply(from, to);

		from = to + 1;
		to = 6 * rate;
		if (to < end) {
			to = end;
		}
		final var sixth = monoBuilder.apply(from, to);
		return Mono.zip(first, second, third, fourth, fifth, sixth)
				.flatMap(tuple -> Mono.just(tuple.getT1() + tuple.getT2() + tuple.getT3() + tuple.getT4() + tuple.getT5() + tuple.getT6()));
	};

	public void calcularTempo(int from, int to, BiFunction<Integer, Integer, Mono<Long>> calc) {
		final Long start = new Date().getTime();
		calc.apply(from, to).subscribe(total -> System.out.println("Soma: " + total));
		final Long end = new Date().getTime();
		System.out.println("Tempo: " + (end - start));
	}
}