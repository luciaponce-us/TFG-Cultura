import type { Season, Series, SeriesStatus } from "../types/series";

export function parseStatus(status: SeriesStatus): string {
  switch (status) {
    case "ONGOING":
      return "En emisión";
    case "FINISHED":
      return "Finalizada";
    case "CANCELLED":
      return "Cancelada";
    case "HIATUS":
      return "En pausa";
  }
}

export function getTrailers(series: Series): string[] {
  const seasons: Season[] = series.seasons;
  const trailers: string[] = [];
  seasons.forEach((season) => {
    if (season.trailerUrl) {
      trailers.push(season.trailerUrl);
    }
  });
  return trailers;
}
