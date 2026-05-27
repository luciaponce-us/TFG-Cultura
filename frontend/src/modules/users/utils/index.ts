export function parsePaymentReceiptUrl(url: string | null): string {
  if (!url) return "";
  if (url.includes("cloudinary")) {
    return (
      "https://docs.google.com/gview?embedded=true&url=" +
      encodeURIComponent(url)
    );
  }
  return url;
}

export function parseUrl(url: string | null): string {
  return url?.trim().split("/").slice(-1)[0] || "";
}
