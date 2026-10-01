import { useState, type Dispatch, type SetStateAction } from "react";

import {
  INITIAL_SERIES,
  type Series,
  type SeriesRequest,
} from "../../types/series";
import { toSeriesRequest } from "../../utils/item.utils";

export function useSeriesForm(
  seriesId: string | undefined,
  seriesToUpdate?: Series,
  isLoading = false,
) {
  const [formOverride, setFormOverride] = useState<{
    seriesId: string | undefined;
    value: SeriesRequest;
  }>();

  const loadedForm = seriesToUpdate
    ? toSeriesRequest(seriesToUpdate)
    : INITIAL_SERIES;

  const form =
    formOverride !== undefined && formOverride.seriesId === seriesId
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<SeriesRequest>> = (nextForm) => {
    if (seriesId && isLoading) {
      return; // Esperando a que se cargue la serie a editar
    }
    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride !== undefined && currentOverride.seriesId === seriesId
          ? currentOverride.value
          : loadedForm;

      return {
        seriesId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return {
    form,
    setForm,
  };
}
