import { UploadBox } from "@/modules/core/components";
import { Box, Grid, Image, Spinner, VStack } from "@chakra-ui/react";
interface ItemImageInputProps {
  image: File | null;
  setImage: (file: File | null) => void;
  loading: boolean;
  placeholder: string;
  disabled?: boolean;
  imageUrl?: string | undefined;
}
export function ItemImageInput({
  image,
  setImage,
  loading,
  placeholder,
  disabled = false,
  imageUrl,
}: ItemImageInputProps) {
  return (
    <Grid
      templateColumns="1fr 2.5fr"
      gap={4}
      w="100%"
      maxW="100%"
      h="100%"
      alignItems="center"
    >
      <Box
        aspectRatio={2 / 3}
        h="auto"
        maxH="100%"
        flexShrink={0}
        alignItems="center"
        justifyContent="center"
        display="flex"
        backgroundColor="gray.100"
        borderRadius="md"
      >
        {loading ? (
          <Spinner size="lg" color="principal.500" />
        ) : (
          <Image
            src={image ? URL.createObjectURL(image) : imageUrl || placeholder}
            alt="Foto del ítem"
            w="100%"
            h="100%"
            objectFit="cover"
            borderRadius="lg"
          />
        )}
      </Box>
      <VStack flex={1} minW={0}>
        <UploadBox
          text={
            <>
              Arrastra la <b>foto del ítem</b>
            </>
          }
          secondaryText="JPG o PNG, tamaño no superior a 2MB"
          fileType="image/*"
          onFileChange={setImage}
          disabled={loading || disabled}
        />
      </VStack>
    </Grid>
  );
}
