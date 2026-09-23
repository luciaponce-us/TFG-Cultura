export function isValidUrl(url: string): boolean {
  const urlPattern =
    /^(https?:\/\/)?([\w-]+(\.[\w-]+)+)(\/[\w-]*)*(\?.*)?(#.*)?$/;
  return urlPattern.test(url);
}

export function isPastOrPresentDate(date: string): boolean {
  const inputDate = new Date(date);
  const today = new Date();
  return inputDate <= today;
}
