import { API_BASE_URL, fetchWithTimeout } from "../utils/utils";

export const fetchDummyData = async (): Promise<string> => {
  try {
    const response = await fetchWithTimeout(`${API_BASE_URL}/api/dummy`, {
      method: "GET",
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.text();
    return data;
  } catch (error) {
    console.error("Error fetching dummy data:", error);
    throw error;
  }
};

export const fetchMongoData = async (): Promise<string> => {
  try {
    const response = await fetchWithTimeout(
      `${API_BASE_URL}/api/dummy/mongodb`,
      {
        method: "GET",
      },
    );
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.text();
    return data;
  } catch (error) {
    console.error("Error fetching MongoDB data:", error);
    throw error;
  }
};
