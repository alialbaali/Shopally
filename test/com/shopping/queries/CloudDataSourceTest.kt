package com.shopping.queries

import com.shopping.DefaultSpec
import com.shopping.dataSourceModule
import com.shopping.domain.CloudDataSource
import com.shopping.domain.model.valueObject.ID
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldNotBeEmpty
import org.koin.test.inject
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class CloudDataSourceTest : DefaultSpec(dataSourceModule) {

    private val cloudDataSource by inject<CloudDataSource>()

    private val emptyFile = createTempFile()

    private val imageFile = createTempFile().apply {
        ImageIO.write(BufferedImage(25, 25, BufferedImage.TYPE_INT_RGB), "png", this)
    }

    init {

        Given("an empty image file") {
            When("uploading the image file") {
                Then("it should fail and throw an exception") {

                    shouldThrowAny {
                        cloudDataSource.uploadImage(emptyFile).getOrThrow()
                    }

                }
            }
        }

        Given("an image file") {
            When("uploading the image file") {
                Then("it should upload successfully") {

                    val imageUrl = shouldNotThrowAny {
                        cloudDataSource.uploadImage(imageFile).getOrThrow()
                    }

                    imageUrl.shouldNotBeEmpty()

                }
            }
        }

        Given("an image file with an image id") {
            And("A folder name") {
                When("uploading the image file to the specified folder") {
                    Then("it should upload successfully") {

                        val imageId = ID.random().toString()
                        val folderName = "Images"

                        val imageUrl = shouldNotThrowAny {
                            cloudDataSource.uploadImage(imageFile, imageId, folderName).getOrThrow()
                        }

                        imageUrl.shouldNotBeEmpty()
                        imageUrl shouldContainIgnoringCase imageId
                        imageUrl shouldContainIgnoringCase folderName

                    }
                }
            }
        }

        Given("an image file with an image id ") {
            When("uploading the image file to the specified folder") {
                And("deleting the file by using the same image id") {
                    Then("it should upload and delete the image successfully") {

                        val imageId = ID.random().toString()

                        val imageUrl = shouldNotThrowAny {
                            cloudDataSource.uploadImage(imageFile, imageId).getOrThrow()
                        }

                        imageUrl.shouldNotBeEmpty()
                        imageUrl shouldContainIgnoringCase imageId


                        shouldNotThrowAny {
                            cloudDataSource.deleteImage(imageId).getOrThrow()
                        } shouldBe Unit

                    }
                }
            }
        }

    }

}